package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.ChannelException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChannelTest {
    @Test
    @DisplayName("private 채널을 생성할 때 name이 있으면 예외가 발생한다")
    void createPrivateChannelFailWhenNameIsNotNull() {
        //when & then
        Assertions.assertThatThrownBy(() -> {Channel channel = Channel.builder()
                        .name("test")
                        .type(ChannelType.PRIVATE)
                        .build(); }).isInstanceOf(ChannelException.class)
                .hasMessage(ChannelErrorCode.PRIVATE_CHANNEL_DOES_NOT_HAVE_NAME.getMessage());
    }

    @Test
    @DisplayName("private 채널을 생성할 때 description이 있으면 예외가 발생한다")
    void createPrivateChannelFailWhenDescriptionIsNotNull() {
        //when & then
        Assertions.assertThatThrownBy(() -> {Channel channel = Channel.builder()
                        .description("test")
                        .type(ChannelType.PRIVATE)
                        .build(); }).isInstanceOf(ChannelException.class)
                .hasMessage(ChannelErrorCode.PRIVATE_CHANNEL_DOES_NOT_HAVE_DESCRIPTION.getMessage());
    }

    @Test
    @DisplayName("private 채널을 생성할 때 name과 description이 있으면 예외가 발생한다")
    void createPrivateChannelFailWhenNameAndDescriptionIsNotNull() {
        //when & then
        Assertions.assertThatThrownBy(() -> {Channel channel = Channel.builder()
                        .name("test")
                        .description("description")
                        .type(ChannelType.PRIVATE)
                        .build(); }).isInstanceOf(ChannelException.class)
                .hasMessage(ChannelErrorCode.PRIVATE_CHANNEL_DOES_NOT_HAVE_NAME_AND_DESCRIPTION.getMessage());
    }
}
