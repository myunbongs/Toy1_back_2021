package com.lookie.toy1_back.tome.service;

import com.lookie.toy1_back.tome.domain.Answer;
import com.lookie.toy1_back.tome.domain.Question;
import com.lookie.toy1_back.tome.domain.User;
import com.lookie.toy1_back.tome.repository.AnswerRepository;
import com.lookie.toy1_back.tome.repository.QuestionRepository;
import com.lookie.toy1_back.tome.repository.UserRepository;
import com.lookie.toy1_back.tome.request.AnswerCreateRequest;
import com.lookie.toy1_back.tome.request.AnswerUpdateRequest;
import com.lookie.toy1_back.tome.request.QuestionCreateRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    private final QuestionService questionService;

    public Answer createAnswer(Long questionId,@Valid AnswerCreateRequest answer) {

        checkVlidateAnswer(answer.getU_num(), questionId);

        Optional<User> userForId = userRepository.findById(answer.getU_num());
        Optional<Question> questionForId = questionRepository.findById(questionId);

        Answer answerToCreate = new Answer();
        BeanUtils.copyProperties(answer, answerToCreate);

        answerToCreate.setUser(userForId.get());
        answerToCreate.setQuestion(questionForId.get());

        return answerRepository.save(answerToCreate);
    }

    public void deleteAnswer(Long answerId) {
        answerRepository.delete(answerRepository.getById(answerId));
    }

    public Answer updateAnswer (Long answerId, AnswerUpdateRequest request) {

        Optional<Answer> findAnswer = answerRepository.findById(answerId);

        if ( !findAnswer.isPresent() ) {
            throw new EntityNotFoundException(
                    "데이터 베이스에 해당하는 답변이 없습니다.");
        }
        Answer answer = findAnswer.get();

        Answer answerToUpdate = new Answer();
        BeanUtils.copyProperties(answer, answerToUpdate);

        answerToUpdate.setContent(request.getContent());
        answerToUpdate.setA_num(answer.getA_num());

        return answerRepository.save(answerToUpdate);
    }

    public void checkVlidateAnswer(Long userId, Long questionId) {
        User user = userRepository.getById(userId);
        Question question = questionRepository.getById(questionId);

        List<Answer> answers = user.getAnswerList();
        for (Answer answer : answers) {
            Question findQuestion = answer.getQuestion();
            if ( question.getQ_num().equals(findQuestion.getQ_num() )) {
                throw new IllegalStateException("이미 답변한 질문입니다.");
            }
        }
    }
}
