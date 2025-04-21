package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItemId(comment.getItem());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    public static Comment toEntity(Comment comment, CommentDto commentDto) {
        comment.setItem(commentDto.getItemId());
        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated() != null ? commentDto.getCreated() : LocalDateTime.now());

        return comment;
    }
}