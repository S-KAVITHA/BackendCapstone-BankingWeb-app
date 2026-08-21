FROM eclipse-temurin:8-jdk

WORKDIR /workspace

# Target-codebase requirements:
# Java 8 + Maven: Spring Boot banking backend and pom.xml
# curl: network boundary verification
# git: review/rollback of agent changes
# Node.js/npm + Claude Code: agentic engineering workflow



RUN apt-get update && apt-get install -y \
    curl \
    git \
    ca-certificates \
    maven \
    nodejs \
    npm \
    && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
RUN mvn dependency:go-offline -B

RUN npm install -g @anthropic-ai/claude-code

RUN mkdir -p /root/.claude
COPY settings.json /root/.claude/settings.json
COPY statusline.sh /root/.claude/statusline.sh
RUN chmod +x /root/.claude/statusline.sh

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

RUN echo 'export PS1="ai-course:\w# "' >> /root/.bashrc && \
    echo 'alias ll="ls -alF"' >> /root/.bashrc

EXPOSE 8085

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["/bin/bash"]
