package com.prograngers.backend.service;

import com.prograngers.backend.dto.solution.response.SolutionListResponse;
import com.prograngers.backend.dto.comment.request.CommentReqeust;
import com.prograngers.backend.dto.solution.reqeust.ScarpSolutionPostRequest;
import com.prograngers.backend.dto.solution.response.SolutionDetailResponse;
import com.prograngers.backend.dto.solution.reqeust.SolutionPatchRequest;
import com.prograngers.backend.dto.solution.reqeust.SolutionPostRequest;
import com.prograngers.backend.dto.solution.response.SolutionUpdateFormResponse;
import com.prograngers.backend.entity.Comment;
import com.prograngers.backend.entity.Problem;
import com.prograngers.backend.entity.Solution;
import com.prograngers.backend.entity.constants.AlgorithmConstant;
import com.prograngers.backend.entity.constants.DataStructureConstant;
import com.prograngers.backend.entity.constants.LanguageConstant;
import com.prograngers.backend.entity.member.Member;
import com.prograngers.backend.exception.notfound.MemberNotFoundException;
import com.prograngers.backend.exception.notfound.ProblemNotFoundException;
import com.prograngers.backend.exception.notfound.SolutionNotFoundException;
import com.prograngers.backend.exception.unauthorization.MemberUnAuthorizedException;
import com.prograngers.backend.exception.unauthorization.UnAuthorizationException;
import com.prograngers.backend.repository.comment.CommentRepository;
import com.prograngers.backend.repository.member.MemberRepository;
import com.prograngers.backend.repository.problem.ProblemRepository;
import com.prograngers.backend.repository.review.ReviewRepository;
import com.prograngers.backend.repository.solution.SolutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolutionService {

    private final SolutionRepository solutionRepository;
    private final CommentRepository commentRepository;

    private final ReviewRepository reviewRepository;

    private final ProblemRepository problemRepository;

    private final MemberRepository memberRepository;



    @Transactional
    public Long save(SolutionPostRequest solutionPostRequest, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
        Solution solution = solutionPostRequest.toEntity();
        solution.updateMember(member);
        Problem problem = problemRepository.findByLink(solution.getProblem().getLink());
        if (problem != null) {
            solution.updateProblem(problem);
        }
        Solution saved = solutionRepository.save(solution);
        return saved.getId();
    }

    @Transactional
    public Long update(Long solutionId, SolutionPatchRequest request, Long memberId) {
        Solution target = findById(solutionId);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        if (target.getId()!=member.getId()){
            throw new MemberUnAuthorizedException();
        }
        Solution solution = request.toEntity(target);
        Solution updated = solutionRepository.save(solution);
        return updated.getId();
    }

    @Transactional
    public void delete(Long solutionId, Long memberId) throws SolutionNotFoundException {
        Solution target = findById(solutionId);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        if (target.getId()!=member.getId()){
            throw new MemberUnAuthorizedException();
        }

        List<Comment> comments = commentRepository.findAllBySolution(target);
        for (Comment comment : comments) {
            comment.updateSolution(null);
            commentRepository.delete(comment);
        }

        solutionRepository.delete(target);
    }

    public Solution findById(Long solutionId) {
        return solutionRepository.findById(solutionId).orElseThrow(() -> new SolutionNotFoundException());
    }

    @Transactional
    public Long saveScrap(Long id, ScarpSolutionPostRequest request, Long memberId) {
        Solution scrap = findById(id);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        // 스크랩 Solution과 사용자가 폼에 입력한 내용을 토대로 새로운 Solution을 만든다
        Solution solution = request.toEntity(scrap);
        solution.updateMember(member);
        Solution saved = solutionRepository.save(solution);
        return saved.getId();
    }

    public SolutionUpdateFormResponse getUpdateForm(Long solutionId, Long memberId) {
        Solution target = findById(solutionId);
        // 로그인한 멤버랑 작성자랑 일치하지 않을 경우 UnAuthorizationException을 던진다
        Long targetMemberId = target.getMember().getId();
        if (targetMemberId != memberId) {
            throw new MemberUnAuthorizedException();
        }

        SolutionUpdateFormResponse solutionUpdateFormResponse = SolutionUpdateFormResponse.toDto(target);
        return solutionUpdateFormResponse;
    }

    public SolutionDetailResponse getSolutionDetail(Long solutionId) {
        Solution solution = findById(solutionId);
        List<Comment> comments = commentRepository.findAllBySolution(solution);
        SolutionDetailResponse solutionDetailResponse = SolutionDetailResponse.toEntity(solution, comments);
        return solutionDetailResponse;
    }

    public SolutionListResponse getSolutionList(
            @PageableDefault(size = 4) Pageable pageable,
            Long problemId,
            LanguageConstant language,
            AlgorithmConstant algorithm,
            DataStructureConstant dataStructure,
            String sortBy) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        PageImpl<Solution> solutions = solutionRepository.getSolutionList(pageable, problem.getId(), language, algorithm, dataStructure, sortBy);

        return SolutionListResponse.from(solutions);
    }
}
