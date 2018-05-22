# https://github.com/elastic/logstash-docker
FROM docker.elastic.co/logstash/logstash:5.5.3

# Add your logstash plugins setup here
# Example: RUN logstash-plugin install logstash-filter-json
RUN logstash-plugin remove x-pack
RUN sed -i '/xpack/d' /usr/share/logstash/config/logstash.yml
