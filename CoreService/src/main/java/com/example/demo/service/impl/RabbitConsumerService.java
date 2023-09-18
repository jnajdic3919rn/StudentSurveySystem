package com.example.demo.service.impl;

import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.domain.survey.Comment;
import com.example.demo.model.dto.rabbitmq.CommentResultDto;
import com.example.demo.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RabbitConsumerService {

  private final CommentRepository commentRepository;

  private final CommentMapper commentMapper;

  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConsumerService.class);

  @RabbitListener(queues={"comment_result"})
  public void consumeCommentResult(CommentResultDto commentResultDto){
    Comment comment = commentMapper.toComment(commentResultDto);

    comment = commentRepository.save(comment);
  }
}
