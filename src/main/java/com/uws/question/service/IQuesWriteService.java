package com.uws.question.service;

import com.uws.core.base.BaseModel;
import com.uws.core.base.IBaseService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.question.QuestionAnswerBaseModel;

/**
 *	我的问卷
 */
public interface IQuesWriteService extends IBaseService {
	
	/**
	 * 问卷查询，分页查询
	 * @param qam
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Page paperSelectedQuery(QuestionAnswerBaseModel qam,int pageNum,int pageSize);

	/**
	 * 获取答卷基本信息
	 * @param id						答卷ID
	 * @return							答卷基本信息对象
	 */
	public QuestionAnswerBaseModel getQuestionAnswerBaseModel(String id);

	/**
	 * 初始化问卷基本信息
	 * @param qabm
	 */
	public void initQuestionNaireBaseInfo(BaseModel bm);

	/**
	 * 校验答题人是否已经回答当前问卷
	 * @param currentUserId	答题人id
	 * @param paperId				
	 * @return	【答卷基础信息对象】
	 */
	public QuestionAnswerBaseModel getQuesNaireBaseModel(String currentUserId,String paperId);

	/**
	 * 删除未提交的答卷
	 * @param answerPaperId
	 */
	public void deleteAnswerPaper(String answerPaperId);

}
