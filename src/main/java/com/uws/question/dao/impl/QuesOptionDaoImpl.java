package com.uws.question.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.domain.question.QuestionOptionModel;
import com.uws.question.dao.IQuesOptionDao;
import com.uws.question.util.Constants;

@Repository("quesOptionDao")
public class QuesOptionDaoImpl  extends BaseDaoImpl implements IQuesOptionDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<QuestionOptionModel> queryOptionByItemId(String itemId) {
		StringBuffer hql = new StringBuffer(" from QuestionOptionModel where 1=1 ");
		hql.append(" and item.id = '"+itemId+"'");
		hql.append(" and status.id= '"+Constants.STATUS_NORMAL.getId()+"'");
		hql.append(" order by seqNum ");
		return this.query(hql.toString());
	}

	@Override
	public void deleteOptionByItemId(String itemId) {
		String hql="delete from QuestionOptionModel where item.id= ? ";
		this.executeHql(hql, itemId);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<QuestionOptionModel> queryOptionByNameAndItemId(String itemId,
			String optionName) {
		StringBuffer hql = new StringBuffer(" from QuestionOptionModel where 1=1 ");
		hql.append(" and item.id = '"+itemId+"'");
		if(!StringUtils.isEmpty(optionName)){
			hql.append(" and optionName = '"+optionName+"'");
		}
		hql.append(" and status.id= '"+Constants.STATUS_NORMAL.getId()+"'");
		hql.append(" order by seqNum ");
		return this.query(hql.toString());
	}

}
