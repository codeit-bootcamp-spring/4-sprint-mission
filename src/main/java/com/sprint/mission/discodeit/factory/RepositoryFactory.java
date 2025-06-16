package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

public class RepositoryFactory {

    private static RepositoryFactory instance;
    private JCFUserRepository jcfUserRepository;
    private JCFChannelRepository jcfChannelRepository;
    private JCFMessageRepository jcfMessageRepository;

    private FileUserRepository fileUserRepository;
    private FileChannelRepository fileChannelRepository;
    private FileMessageRepository fileMessageRepository;

    public static RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    private RepositoryFactory() {
        jcfUserRepository = JCFUserRepository.getInstance();
        jcfChannelRepository = JCFChannelRepository.getInstance();
        jcfMessageRepository = JCFMessageRepository.getInstance();

        fileUserRepository = FileUserRepository.getInstance();
        fileChannelRepository = FileChannelRepository.getInstance();
        fileMessageRepository = FileMessageRepository.getInstance();
    }

    public JCFUserRepository getJCFUserRepository() {
        if (jcfUserRepository == null) {
            jcfUserRepository = new JCFUserRepository();
        }
        return jcfUserRepository;
    }

    public JCFChannelRepository getJCFChannelRepository() {
        if (jcfChannelRepository == null) {
            jcfChannelRepository = new JCFChannelRepository();
        }
        return jcfChannelRepository;
    }

    public JCFMessageRepository getJCFMessageRepository() {
        if (jcfMessageRepository == null) {
            jcfMessageRepository = new JCFMessageRepository();
        }
        return jcfMessageRepository;
    }

    public FileUserRepository getFileUserRepository() {
        if (fileUserRepository == null) {
            fileUserRepository = new FileUserRepository();
        }
        return fileUserRepository;
    }

    public FileChannelRepository getFileChannelRepository() {
        if (fileChannelRepository == null) {
            fileChannelRepository = new FileChannelRepository();
        }
        return fileChannelRepository;
    }

    public FileMessageRepository getFileMessageRepository() {
        if (fileMessageRepository == null) {
            fileMessageRepository = new FileMessageRepository();
        }
        return fileMessageRepository;
    }
}
