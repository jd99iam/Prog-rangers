package com.prograngers.backend.dto;

import com.prograngers.backend.entity.*;
import com.prograngers.backend.entity.constants.AlgorithmConstant;
import com.prograngers.backend.entity.constants.DataStructureConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolutionPatchRequest {
    @NotBlank(message = "풀이 제목을 입력해주세요")
    private String title;
    private AlgorithmConstant algorithmName;
    private DataStructureConstant dataStructureName;
    @NotBlank(message = "소스 코드를 입력해 주세요")
    private String code;

    @NotBlank(message = "풀이 설명을 입력해주세요")
    private String description;

    public Solution toEntity(Solution target) {
        target.updateTitle(title);
        target.updateAlgorithm(algorithmName);
        target.updateDataStructure(dataStructureName);
        target.updateCode(code);
        target.updateDescription(description);
        return target;
    }
}
