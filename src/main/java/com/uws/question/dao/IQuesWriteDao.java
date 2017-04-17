package com.uws.question.dao;

import com.uws.core.hibernate.dao.IBaseDao;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.question.QuestionAnswerBaseModel;

/**
 *	我的问卷
 */
public interface IQuesWriteDao extends IBaseDao {
	
	/**
	 * 问卷查询分页查询
	 * @param qam
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Page paperSelectedQuery(QuestionAnswerBaseModel qam,int pageNum,int pageSize);

	/**
	 * 获取答卷基本信息
	 * @param id				答卷ID
	 * @return					答卷基本信息对象
	 */
	public QuestionAnswerBaseModel getQuestionAnswerBaseModel(String id);

	/**
	 * 判断答题人是否已经回答当前问卷
	 * @param currentUserId	答题人
	 * @param paperId				问卷id
	 * @return									答卷基本信息对象
	 */
	public QuestionAnswerBaseModel getQuesNaireBaseModel(String currentUserId,String paperId);

	/**
	 * 删除未提交的答卷
	 * @param answerPaperId		答卷ID
	 */
	public void deleteAnswerPaper(String answerPaperId);
	
	/**
	 * 级联删除答卷的答案
	 * @param answerPaperId		答卷ID
	 */
	public void deleteAnswerPaperDetailCascade(String answerPaperId);
}
