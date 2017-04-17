package com.uws.question.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.base.BaseTeacherModel;
import com.uws.domain.orientation.StudentInfoModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionOptionModel;
import com.uws.question.dao.IQuesStatisticDao;
import com.uws.question.service.IQuesStatisticService;
import com.uws.sys.model.Dic;

/**
 * 问卷统计service实现
 */
@Service("quesStatisticService")
public class QuesStatisticServiceImpl extends BaseServiceImpl implements
		IQuesStatisticService {

	@Autowired
	private IQuesStatisticDao quesStatisticDao;

	public Page queryQuesInfo(Integer pageSize, Integer pageNo,
			QuestionInfoModel question) {
		return quesStatisticDao.queryQuesInfo(pageSize, pageNo, question);
	}

	@Override
	public long countQuesAnswer(String questionId, Dic status) {
		return quesStatisticDao.countQuesAnswer(questionId, status);
	}

	@Override
	public long countItemAnswer(String questionId, String itemId,
			String optionId, Dic itemType) {
		return quesStatisticDao.countItemAnswer(questionId, itemId, optionId,
				itemType);
	}

	@Override
	public List<QuestionOptionModel> queryOptionList(String questionId,
			String itemId) {
		return quesStatisticDao.queryOptionList(questionId, itemId);
	}

	@Override
	public Page queryStudentsByQuestion(Integer pageSize, Integer pageNo,
			StudentInfoModel stu, String queid) {
		return quesStatisticDao.queryStudentsByQuestion(pageSize, pageNo, stu,
				queid);
	}

	@Override
	public Page queryTeachersByQuestion(Integer pageSize, Integer pageNo,
			BaseTeacherModel tea, String queid) {
		return quesStatisticDao.queryTeachersByQuestion(pageSize, pageNo, tea,
				queid);
	}

	/**
	 * 获取问卷的所有答案信息
	 * 
	 * @Description: TODO
	 * @author: 唐靖
	 * @date: 2016-11-30 下午3:31:21
	 * @param queid
	 * @return
	 */
	public List<Object> queryQuestionAnswerByQuestionId(String queid) {
		return quesStatisticDao.queryQuestionAnswerByQuestionId(queid);
	}

}
