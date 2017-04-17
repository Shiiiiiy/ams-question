package com.uws.question.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.core.util.HqlEscapeUtil;
import com.uws.domain.question.QuestionItemModel;
import com.uws.question.dao.IQuesItemDao;
import com.uws.question.util.Constants;

@Repository("quesItemDao")
public class QuesItemDaoImpl  extends BaseDaoImpl implements IQuesItemDao{

	@Override
	public Page queryQuesItem(Integer pageSize, Integer pageNo,
			QuestionItemModel quesItem) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" from QuestionItemModel where 1=1 ");
		if(quesItem!=null){
			
			//题目名称
			if (!StringUtils.isEmpty(quesItem.getItemName())) 
			{
				hql.append(" and itemName like ? ");
				if (HqlEscapeUtil.IsNeedEscape(quesItem.getItemName().trim())) 
				{
					values.add("%" + HqlEscapeUtil.escape(quesItem.getItemName().trim()) + "%");
					hql.append(HqlEscapeUtil.HQL_ESCAPE);
				} else
					values.add("%" + quesItem.getItemName() + "%");
			}
			
			//题型
			if (null!= quesItem.getItemType() && StringUtils.isNotEmpty(quesItem.getItemType().getId()) ) 
			{
				hql.append(" and  itemType.id = ? ");
				values.add(quesItem.getItemType().getId());
			}
			
			//创建日期_开始时间  
			if(!StringUtils.isEmpty(quesItem.getBeginTime())){
				hql.append(" and to_char(createTime ,'yyyy-MM-dd') >= ? ");
				values.add(quesItem.getBeginTime());
			}
			
			//创建日期_结束时间
			if(!StringUtils.isEmpty(quesItem.getEndTime())){
				hql.append(" and to_char(createTime ,'yyyy-MM-dd') <= ? ");
				values.add(quesItem.getEndTime());
			}
			
			//题目分类
			if (null!= quesItem.getItemCategory() && StringUtils.isNotEmpty(quesItem.getItemCategory().getId()) ) 
			{
				hql.append(" and  itemCategory.id = ? ");
				values.add(quesItem.getItemCategory().getId());
			}
			
			//状态（启用、禁用、保存）
			if (null!= quesItem.getUseStatus() && StringUtils.isNotEmpty(quesItem.getUseStatus().getId()) ) 
			{
				hql.append(" and  useStatus.id = ? ");
				values.add(quesItem.getUseStatus().getId());
			}
		}
		
		//删除状态
		hql.append(" and status.id = ?  ");
		values.add(Constants.STATUS_NORMAL.getId());
		
		//排序条件
		hql.append(" order by updateTime desc ");
		
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<QuestionItemModel> queryQuesItemByName(String itemName,String id, String itemCategoryId, String itemTypeId) {
		
		StringBuffer hql = new StringBuffer(" from QuestionItemModel where 1=1 ");
		hql.append(" and itemName ='"+itemName+"'");
		hql.append(" and itemCategory.id ='"+itemCategoryId+"'");
		hql.append(" and itemType.id ='"+itemTypeId+"'");
		hql.append(" and status.id ='"+Constants.STATUS_NORMAL.getId()+"'");
		return this.query(hql.toString());
	}

	@Override
	public Page queryItemCompList(int pageSize, int pageNo, String itemName,String itemTypeId, String itemCategoryId,String paperType) {
		List<Object> values = new ArrayList<Object>();
		
		StringBuffer hql = new StringBuffer(" from QuestionItemModel where 1=1 ");
		
		//题目名称
		if (!StringUtils.isEmpty(itemName)) 
		{
			hql.append(" and itemName like ? ");
			if (HqlEscapeUtil.IsNeedEscape(itemName)) 
			{
				values.add("%" + HqlEscapeUtil.escape(itemName) + "%");
				hql.append(HqlEscapeUtil.HQL_ESCAPE);
			} else
				values.add("%" + itemName + "%");
		}
		
		//题型
		if (!StringUtils.isEmpty(itemTypeId)) 
		{
			hql.append(" and itemType.id =? ");
			values.add(itemTypeId);
		}
		
		//【公共类型问卷，可查询所有问题】【其他类型问卷，可查询(本身+公共)类型题目】
		String commonType = Constants.QUESINFO_TYPE_COMMON.getId();
		if (!StringUtils.isEmpty(itemCategoryId)) 
		{
			hql.append(" and itemCategory.id =? ");
			values.add(itemCategoryId);
		}else{
			if(!commonType.equals(paperType)){
				hql.append(" and itemCategory.id in('"+paperType+"','"+commonType+"')");
			}
		}
		
		//启用状态
		hql.append(" and useStatus.id =?");
		values.add(Constants.PAPER_STATUS_ENABLE.getId());
		
		//删除状态
		hql.append(" and status.id = ?  ");
		values.add(Constants.STATUS_NORMAL.getId());
		
		if (values.size() == 0)
			return this.pagedQuery(hql.toString(), pageNo, pageSize);
		else
			return this.pagedQuery(hql.toString(), pageNo, pageSize,values.toArray());
	}

	@Override
	public QuestionItemModel queryItemById(String itemId) {
		StringBuffer hql = new StringBuffer(" from QuestionItemModel where 1=1 ");
		hql.append(" and id=?");
		if(DataUtil.isNotNull(itemId)){
			return (QuestionItemModel)this.queryUnique(hql.toString(), new Object[]{itemId});
		}
		return new QuestionItemModel();
	}
}
