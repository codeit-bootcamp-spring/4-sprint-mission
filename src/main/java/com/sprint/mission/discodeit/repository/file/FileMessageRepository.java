package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileMessageRepository implements MessageRepository, Serializable {

    private static final String USER_FILE_PATH = "./data/message.ser";
    private static final FileMessageRepository instance = new FileMessageRepository();

    public FileMessageRepository() {}

    public static FileMessageRepository getInstance() {
        return instance;
    }

    private List<Message> loadMessages(){
        File file = new File(USER_FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<Message>(); // 해당 파일이 없거나 비어 있다면 빈 ArrayList를 반환
        }
        // ArrayList<User> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE_PATH))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveMessages(List<Message> messages){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message createMessage(User user, Channel channel, String contents){
        if(channel == null) {
            //System.out.println("채널이 null 입니다.");
            return null;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 작성할 수 없습니다.", user.getUserName());
            return null;
        }
        //System.out.printf("메세지를 추가합니다. User: %s, ChannelId: %s, Contents: %s%n", user.getId(), channel.getId(), contents);
        List<Message> messages = loadMessages();
        Message newMessage = new Message(user, channel, contents);
        newMessage.registerMessageToUserAndChannel(user, channel);
        messages.add(newMessage);

        List<User> users = FileUserRepository.getInstance().getUsers();

        for(User userFromFile : users) {
            if(userFromFile.getId().equals(user.getId())) {
                userFromFile.addMessage(newMessage);
                break;
            }
        }

        List<Channel> channels = FileChannelRepository.getInstance().getAllChannels();
        for(Channel channelFromFile : channels) {
            if(channelFromFile.getId().equals(channel.getId())) {
                channelFromFile.addMessage(newMessage);
            }
        }

        saveMessages(messages);
        FileUserRepository.getInstance().saveUsers(users);
        FileChannelRepository.getInstance().saveChannels(channels);

        return newMessage;
    }

    @Override
    public void deleteMessage(User user, Message message){
        if(message == null) {
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            return;
        }
        if (!user.getId().equals(message.getUser().getId())) {
            return;
        }
        List<Message> messagesFromFile = loadMessages();
        messagesFromFile.remove(message);
        for( Message messageFromFile : messagesFromFile) {
            if(messageFromFile.getId().equals(message.getId())) {
                messagesFromFile.remove(messageFromFile);
                break;
            }
        }


        List<User> usersFromFile = FileUserRepository.getInstance().getUsers();
        for(User userFromFile : usersFromFile) {
            if(userFromFile.getId().equals(user.getId())) {
                for( Message messageFromUserFromFile : userFromFile.getMessages()) {
                    if(messageFromUserFromFile.getId().equals(message.getId())) {
                        userFromFile.removeMessage(messageFromUserFromFile);
                        break;
                    }
                }
            }
        }

        List<Channel> channelsFromFile = FileChannelRepository.getInstance().getAllChannels();
        for(Channel channelFromFile : channelsFromFile) {
            if(channelFromFile.getId().equals(message.getChannel().getId())) {
                for( Message messageFromChannelFromFile : channelFromFile.getMessages()) {
                    if(messageFromChannelFromFile.getId().equals(message.getId())) {
                        channelFromFile.removeMessage(messageFromChannelFromFile);
                        break;
                    }
                }
            }
        }

        saveMessages(messagesFromFile);
        FileUserRepository.getInstance().saveUsers(usersFromFile);
        FileChannelRepository.getInstance().saveChannels(channelsFromFile);
    }

    @Override
    public void updateMessage(User user, Message message, String newContents){
        if(message == null) {
            //System.out.println("메세지가 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            //System.out.printf("'%s' 비활성 상태라 메세지를 업데이트할 수 없습니다.", user.getUserName());
            return;
        }
        if(!user.equals(message.getUser())) {
            //System.out.printf("'%s'는 '%s' 메시지의 주인이 아닙니다. 따라서 해당 메시지를 '%s'로 바꾸는 것은 불가능합니다.", user.getUserName(), message.getMessageContents(), newContents);
            return;
        }

        List<Message> messagesFromFile = loadMessages();
        for (Message messageFromFile : messagesFromFile) {
            if(messageFromFile.getId().equals(message.getId())) {
                messageFromFile.updateMessageContent(newContents);
                break;
            }
        }

        List<User> usersFromFile = FileUserRepository.getInstance().getUsers();
        /*
        for (User userFromFile : usersFromFile) {
            if(userFromFile.getId().equals(user.getId())) {
                // 유저를 찾음
                for(Message messageFromUser : userFromFile.getMessages()) {
                    if(messageFromUser.getId().equals(message.getId())) {
                        messageFromUser.updateMessageContent(newContents);
                        break;
                    }
                }
            }
        }*/
        usersFromFile.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .flatMap(u -> u.getMessages().stream())
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst()
                .ifPresent(m -> m.updateMessageContent(newContents));
        // 위 for문을 stream으로 변환함

        List<Channel> channelsFromFile = FileChannelRepository.getInstance().getAllChannels();
        /*
        for (Channel channelFromFile : channelsFromFile) {
            if(channelFromFile.getId().equals(message.getChannel().getId())) {
                // 속한 채널 찾음
                for(Message messageFromChannelFromFile : channelFromFile.getMessages()) {
                    if(messageFromChannelFromFile.getId().equals(message.getId())) {
                        messageFromChannelFromFile.updateMessageContent(newContents);
                        break;
                    }
                }
            }
        }*/
        // 끔찍한 코드 개선 요망!!

        channelsFromFile.stream()
                .filter(ch -> ch.getId().equals(message.getChannel().getId()))
                .flatMap(ch -> ch.getMessages().stream())
                .filter(msg -> msg.getId().equals(message.getId()))
                .findFirst()
                .ifPresent(msg -> msg.updateMessageContent(newContents));

        saveMessages(messagesFromFile);
        FileUserRepository.getInstance().saveUsers(usersFromFile);
        FileChannelRepository.getInstance().saveChannels(channelsFromFile);
    }

    @Override
    public List<Message> getMessages() {
        return loadMessages();
    }

}
