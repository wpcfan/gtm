import { MatIconRegistry } from '@angular/material';
import { DomSanitizer } from '@angular/platform-browser';
import * as _ from 'lodash';

/**
 * 加载图标，包括 svg 图标和 FontAwesome 字体图标等
 *
 * @param ir a MatIconRegistry 实例，用于注册图标资源
 * @param ds a DomSanitizer 实例，用于忽略安全检查返回一个 URL
 */
export const loadIconResources = (ir: MatIconRegistry, ds: DomSanitizer) => {
  const imgDir = 'assets/img';
  const avatarDir = `${imgDir}/avatar`;
  const sidebarDir = `${imgDir}/sidebar`;
  const iconDir = `${imgDir}/icons`;
  const dayDir = `${imgDir}/days`;
  ir
    .addSvgIconSetInNamespace(
      'avatars',
      ds.bypassSecurityTrustResourceUrl(`${avatarDir}/avatars.svg`)
    )
    .addSvgIcon(
      'unassigned',
      ds.bypassSecurityTrustResourceUrl(`${avatarDir}/unassigned.svg`)
    )
    .addSvgIcon(
      'project',
      ds.bypassSecurityTrustResourceUrl(`${sidebarDir}/project.svg`)
    )
    .addSvgIcon(
      'projects',
      ds.bypassSecurityTrustResourceUrl(`${sidebarDir}/projects.svg`)
    )
    .addSvgIcon(
      'month',
      ds.bypassSecurityTrustResourceUrl(`${sidebarDir}/month.svg`)
    )
    .addSvgIcon(
      'week',
      ds.bypassSecurityTrustResourceUrl(`${sidebarDir}/week.svg`)
    )
    .addSvgIcon(
      'day',
      ds.bypassSecurityTrustResourceUrl(`${sidebarDir}/day.svg`)
    )
    .addSvgIcon(
      'move',
      ds.bypassSecurityTrustResourceUrl(`${iconDir}/move.svg`)
    )
    .registerFontClassAlias('fontawesome', 'fa');
  const days = _.range(1, 31);
  days.forEach(day =>
    ir.addSvgIcon(
      `day${day}`,
      ds.bypassSecurityTrustResourceUrl(`${dayDir}/day${day}.svg`)
    )
  );
};
