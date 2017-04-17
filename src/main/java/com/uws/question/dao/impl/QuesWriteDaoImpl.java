package com.uws.question.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uws.common.util.AmsDateUtil;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.question.QuestionAnswerBaseModel;
import com.uws.question.dao.IQuesWriteDao;
import com.uws.question.util.Constants;

@Repository("quesWriteDao")
public class QuesWriteDaoImpl extends BaseDaoImpl implements IQuesWriteDao {
	
	SessionUtil sessionUtil = SessionFactory.getSession(Constants.QUESTION_MANAGE);

	@Override
	public Page paperSelectedQuery(QuestionAnswerBaseModel qabm, int pageNum,int pageSize) {
		
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" from QuestionAnswerBaseModel qabm where 1=1");
		
		/**
		 * 问卷名称
		 */
		if(DataUtil.isNotNull(qabm.getQuestionInfo()) && DataUtil.isNotNull(qabm.getQuestionInfo().getName())){
			hql.append(" and qabm.questionInfo.name like ?");
			values.add("%"+qabm.getQuestionInfo().getName()+"%");
		}
		
		/**
		 * 问卷类型
		 */
		if(DataUtil.isNotNull(qabm.getQuestionInfo())){
			if(DataUtil.isNotNull(qabm.getQuestionInfo().getTypeDic()) && DataUtil.isNotNull(qabm.getQuestionInfo().getTypeDic().getId())){
				String id = qabm.getQuestionInfo().getTypeDic().getId();
				hql.append(" and qabm.questionInfo.typeDic.id=?");
				values.add(id);
			}
		}
		
		/**
		 * 答题状态
		 */
			if(DataUtil.isNotNull(qabm.getAnswerStatus()) && DataUtil.isNotNull(qabm.getAnswerStatus().getId())){
				String id = qabm.getAnswerStatus().getId();
				hql.append(" and qabm.answerStatus.id=?");
				values.add(id);
			}
		
		/**
		 * 发布日期
		 */
			if(DataUtil.isNotNull(qabm.getBeginDate()) && DataUtil.isNotNull(qabm.getStopDate())){
				hql.append(" and qabm.createTime>= ?");
				values.add(AmsDateUtil.toTime(qabm.getBeginDate()));
				hql.append(" and qabm.createTime<= ?");
				values.add(AmsDateUtil.toTime(qabm.getStopDate()));
			} else if(DataUtil.isNotNull(qabm.getBeginDate()) && DataUtil.isNull(qabm.getStopDate())) {
				hql.append(" and qabm.createTime>= ?");
				values.add(AmsDateUtil.toTime(qabm.getBeginDate()));
			} else if(DataUtil.isNull(qabm.getBeginDate()) && DataUtil.isNotNull(qabm.getStopDate())) {
				hql.append(" and qabm.createTime<= ?");
				values.add(AmsDateUtil.toTime(qabm.getStopDate()));
			}
		
		hql.append(" and qabm.respondent.id=?");
		values.add(this.sessionUtil.getCurrentUserId());
		
		//排序条件
		hql.append(" order by qabm.updateTime desc ");
		
	    Page page = null;
	    
	    if(values.size()>0){
	    	page = this.pagedQuery(hql.toString(), pageNum, pageSize, values.toArray());
	    }else{
	    	page = this.pagedQuery(hql.toString(), pageNum, pageSize);
	    }
		return page;
	}

	@Override
	public QuestionAnswerBaseModel getQuestionAnswerBaseModel(String id) {
		if(DataUtil.isNotNull(id)){
			String hql = " from QuestionAnswerBaseModel qabm where qabm.id=?";
			return (QuestionAnswerBaseModel)this.queryUnique(hql, new Object[]{id});
		}else{
			return new QuestionAnswerBaseModel();
		}
	}

	/**
	 * 获取当前登录人保存的答卷
	 * @param currentUserId					答题人
	 * @param questionNaireId				问卷Id
	 * @return													当前登陆人保存的问卷
	 */
	@Override
	public QuestionAnswerBaseModel getQuesNaireBaseModel(String currentUserId,String questionNaireId) {
		QuestionAnswerBaseModel qabm = null;
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer(" from QuestionAnswerBaseModel qabm where 1=1");
		if(DataUtil.isNotNull(currentUserId) && DataUtil.isNotNull(questionNaireId)){
			hql.append(" and qabm.respondent.id=?");
			hql.append(" and qabm.questionInfo.id=?");
			values.add(currentUserId);
			values.add(questionNaireId);
			qabm = (QuestionAnswerBaseModel)this.queryUnique(hql.toString(), values.toArray());
		}
		return qabm;
	}

	@Override
	public void deleteAnswerPaper(String answerPaperId) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer();
		if(DataUtil.isNotNull(answerPaperId)){
			hql.append(" delete from QuestionAnswerBaseModel qabm where qabm.id=?");
			values.add(answerPaperId);
			this.executeHql(hql.toString(), values.toArray());
		}
	}
	
	@Override
	public void deleteAnswerPaperDetailCascade(String answerPaperId) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer();
		if(DataUtil.isNotNull(answerPaperId)){
			hql.append(" delete from QuestionAnswerDetailModel aqdm where aqdm.answerBaseInfo.id=?");
			values.add(answerPaperId);
			this.executeHql(hql.toString(), values.toArray());
		}
	}

}
