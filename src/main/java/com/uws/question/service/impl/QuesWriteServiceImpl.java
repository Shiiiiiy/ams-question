package com.uws.question.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.base.BaseModel;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.question.QuestionAnswerBaseModel;
import com.uws.question.dao.IQuesWriteDao;
import com.uws.question.service.IQuesWriteService;

@Service("quesWriteService")
public class QuesWriteServiceImpl extends BaseServiceImpl implements IQuesWriteService {
	
	@Autowired 
	private IQuesWriteDao quesWriteDao;

	@Override
	public Page paperSelectedQuery(QuestionAnswerBaseModel qam, int pageNum,int pageSize) {
		
		return this.quesWriteDao.paperSelectedQuery(qam, pageNum, pageSize);
	}

	@Override
	public QuestionAnswerBaseModel getQuestionAnswerBaseModel(String id) {

		return this.quesWriteDao.getQuestionAnswerBaseModel(id);
	}

	@Override
	public void initQuestionNaireBaseInfo(BaseModel bm) {
		this.quesWriteDao.save(bm);
	}

	@Override
	public QuestionAnswerBaseModel getQuesNaireBaseModel(String currentUserId,String paperId) {
		return this.quesWriteDao.getQuesNaireBaseModel(currentUserId, paperId);
	}

	@Override
	public void deleteAnswerPaper(String answerPaperId) {
		// 级联删除答卷的答案详细信息
		this.quesWriteDao.deleteAnswerPaperDetailCascade(answerPaperId);
		// 删除答卷的基础信息
		this.quesWriteDao.deleteAnswerPaper(answerPaperId);
	}

}
