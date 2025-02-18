package com.prograngers.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolutionDetailResponseComment {
    String nickname;
    Integer orderParent;
    Integer orderChild;
    String content;
    String mention;
}
