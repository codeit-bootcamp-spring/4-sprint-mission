package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;

public class FileMessageService implements MessageService {
    private static final FileMessageService instance = new FileMessageService();
    private final ArrayList<Message> data;

    public FileMessageService() {
        this.data = loadMessages();
    }
    public ArrayList<Message> getMessages() {
        return data;
    }
    public static FileMessageService getInstance() {
        return instance;
    }

    public void saveMessages(){
        System.out.println("메세지 리스트 저장");
        String filePath = "./data/messages.ser";

        // ArrayList<Message> 직렬화및 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
            System.out.println("Message 리스트가 직렬화되어 '" + filePath + "' 파일에 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileChannelService.getInstance().saveChannels(); //메시지가 변경되면 채널과 유저 둘 다변경 되어야 함
    }

    public ArrayList<Message> loadMessages(){
        ArrayList<Message> deserializedMessages = null;
        System.out.println("메세지 리스트 불러오기");
        String filePath = "./data/messages.ser"; // 유저 직렬화

        if (!new File(filePath).exists() || new File(filePath).length() == 0) {
            return new ArrayList<Message>(); // 해당 파일이 없으면 빈 ArrayList를 반환
        }

        // ArrayList<Message> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            deserializedMessages = (ArrayList<Message>) ois.readObject();
            /*
            System.out.println("역직렬화된 Message 리스트 정보:");

            for (Message message : deserializedMessages) {
                System.out.println("------------");
                System.out.println("Message: " + message.toString());
            }*/

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return deserializedMessages;
    }

    @Override
    public Message createMessage(User user, Channel channel, String contents){
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return null;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s' 비활성 상태라 메세지를 작성할 수 없습니다.", user.getUserName());
            return null;
        }
        System.out.printf("메세지를 추가합니다. User: %s, ChannelId: %s, Contents: %s%n",
                user.getId(), channel.getId(), contents);
        Message newMessage = new Message(user, channel, contents);
        data.add(newMessage);
        newMessage.registerMessageToUserAndChannel(user, channel);

        saveMessages();
        return newMessage;
    }

    @Override
    public void deleteMessage(User user, Message message){
        if(message == null) {
            System.out.println("메세지가 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s' 비활성 상태라 메세지를 삭제할 수 없습니다.", user.getUserName());
            return;
        }
        if (user.getId().equals(message.getUser().getId())) {
            System.out.printf("메세지를 삭제합니다. User: %s, MessageId: %s, Contents: %s%n",
                    user.getUserName(), message.getId(), message.getMessageContents());
        } else {
            System.out.printf("'%s' 은 '%s' 메시지 주인이 아닙니다%n",
                    user.getUserName(), message.getId());
            return;
        }
        message.getChannel().getMessages().remove(message); // 채널에서 메세지 삭제
        user.removeMessage(message);
        data.remove(message);

        saveMessages();
    }

    @Override
    public void updateMessage(User user, Message message, String newContents){
        if(message == null) {
            System.out.println("메세지가 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s' 비활성 상태라 메세지를 업데이트할 수 없습니다.", user.getUserName());
            return;
        }
        if(!user.equals(message.getUser())) {
            System.out.printf("'%s'는 '%s' 메시지의 주인이 아닙니다. 따라서 해당 메시지를 '%s'로 바꾸는 것은 불가능합니다.", user.getUserName(), message.getMessageContents(), newContents);
            return;
        }

        System.out.println("메세지를 업데이트 합니다.");
        System.out.printf("이전 메세지: %s%n", message.getMessageContents());
        System.out.printf("현재 메세지: %s%n", newContents);
        message.updateMessageContent(newContents);

        saveMessages();
    }

    @Override
    public void printMessagesByChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        System.out.printf("%s 채널의 메세지를 조회합니다. 메시지 수: %d%n",
                channel.getChannelName(), channel.getMessages().size());
        data.stream()
                .filter(message -> message.getChannel().equals(channel))
                .forEach(message -> System.out.printf("작성자: %s, 내용: %s%n",
                        message.getUser().getUserName(), message.getMessageContents()));
    }

    @Override
    public void printMessage(Message message) {
        if(message == null) {
            System.out.println("메세지가 null 입니다.");
            return;
        }
        System.out.printf("메세지 ID: %s 의 정보를 출력합니다.%n", message.getId());
        System.out.println(message.toString());
    }

    @Override
    public void printAllMessage() {
        System.out.printf("전체 메시지를 출력합니다. 메세지 수: %d%n", data.stream()
                .filter(message -> message.getUser().getStatus().equals(UserStatus.ACTIVE))
                .toList().size());
        data
                .stream().filter(message -> message.getUser().getStatus().equals(UserStatus.ACTIVE))
                .forEach(message -> System.out.printf("작성자: %s, 내용: %s%n", message.getUser().getUserName(), message.getMessageContents()));
    }

    @Override
    public void printAllMessageByUser(User user) {
        System.out.printf("%s이 작성한 모든 메세지를 출력합니다. 메시지 수: %d%n",
                user.getUserName(), user.getMessages().size());
        user.getMessages()
                .forEach(message -> System.out.printf("내용: %s%n", message.getMessageContents()));
    }
}
