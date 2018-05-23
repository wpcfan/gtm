package dev.local.gtm.api.changelogs;

import java.util.Arrays;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dev.local.gtm.api.domain.Authority;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.security.AuthoritiesConstants;
import dev.local.gtm.api.web.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * 进行数据库的 migration
 */
@Slf4j
@RequiredArgsConstructor
@ChangeLog(order = "001")
public class InitialSetup {

  @ChangeSet(order = "001", id = "001_add_default_roles", author = "Peng Wang")
  public void addDefaultRoles(MongoTemplate mongoTemplate) {
    detectAndCreateRole(mongoTemplate, AuthoritiesConstants.USER);
    detectAndCreateRole(mongoTemplate, AuthoritiesConstants.ADMIN);
  }

  @ChangeSet(order = "002", id = "002_add_default_users", author = "Peng Wang")
  public void addDefaultUsers(MongoTemplate mongoTemplate) {
    val encoder = new BCryptPasswordEncoder();
    val roleUser = getAuthorityByName(mongoTemplate, AuthoritiesConstants.USER);
    val roleAdmin = getAuthorityByName(mongoTemplate, AuthoritiesConstants.ADMIN);
    val admin = User.builder()
      .name("超级管理员")
      .login("admin")
      .password(encoder.encode("admin"))
      .activated(true)
      .email("admin@local.dev")
      .mobile("13999999999")
      .avatar("avatar-0")
      .authorities(Arrays.asList(roleUser, roleAdmin))
      .build();
    mongoTemplate.save(admin);
  }

  private void detectAndCreateRole(MongoTemplate mongoTemplate, String roleName) {
    val query = new Query();
    query.addCriteria(Criteria.where("name").is(roleName));
    val userCount = mongoTemplate.count(query, Authority.class);
    if (userCount > 1) {
      log.error("Authority 中有重复的角色名称 {}", roleName);
      throw new InternalServerErrorException("Authority 中有重复的角色名称");
    }
    if (userCount == 0) {
      mongoTemplate.save(new Authority(roleName));
    }
  }

  private Authority getAuthorityByName(final MongoTemplate mongoTemplate, final String name) {
    val query = new Query();
    query.addCriteria(Criteria.where("name").is(name));
    return mongoTemplate.findOne(query, Authority.class);
  }
}
