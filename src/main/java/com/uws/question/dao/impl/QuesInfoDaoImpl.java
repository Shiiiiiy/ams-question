package com.uws.question.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uws.common.util.QuestionNaireConstants;
import com.uws.core.base.BaseModel;
import com.uws.core.hibernate.dao.impl.BaseDaoImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionItemOptionModel;
import com.uws.domain.question.QuestionNaireRespondentModel;
import com.uws.question.dao.IQuesInfoDao;
import com.uws.question.util.Constants;

/**
 * 调查问卷维护dao实现
 */
@Repository("quesInfoDao")
@SuppressWarnings("all")
public class QuesInfoDaoImpl extends BaseDaoImpl implements IQuesInfoDao {

	@Override
	public Page queryQuesInfo(QuestionInfoModel questionInfo,int pageNum,int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" from QuestionInfoModel qim where 1=1");
		/**
		 * 问卷名称
		 */
		if(DataUtil.isNotNull(questionInfo.getName())){
			hql.append(" and qim.name like ?");
			values.add("%"+questionInfo.getName()+"%");
		}
		
		/**
		 * 问卷类型
		 */
		if(DataUtil.isNotNull(questionInfo.getTypeDic()) && DataUtil.isNotNull(questionInfo.getTypeDic().getId())){
			String id = questionInfo.getTypeDic().getId();
			hql.append(" and qim.typeDic.id=?");
			values.add(id);
		}
		
		/**
		 * 问卷状态
		 */
		if(DataUtil.isNotNull(questionInfo.getStatusDic())&&DataUtil.isNotNull(questionInfo.getStatusDic().getId())){
			String id = questionInfo.getStatusDic().getId();
			hql.append(" and qim.statusDic.id=?");
			values.add(id);
		}
		
		/**
		 * 发布日期
		 */
	    if(DataUtil.isNotNull(questionInfo.getBeginDate()) && DataUtil.isNotNull(questionInfo.getStopDate())){
	    	hql.append(" and qim.releaseDate>= ?");
	    	values.add(questionInfo.getBeginDate());
	    	hql.append(" and qim.releaseDate<= ?");
	    	values.add(questionInfo.getStopDate());
	    } else if(DataUtil.isNotNull(questionInfo.getBeginDate()) && DataUtil.isNull(questionInfo.getStopDate())) {
	    	hql.append(" and qim.releaseDate>= ?");
	    	values.add(questionInfo.getBeginDate());
	    } else if(DataUtil.isNull(questionInfo.getBeginDate()) && DataUtil.isNotNull(questionInfo.getStopDate())) {
	    	hql.append(" and qim.releaseDate<= ?");
	    	values.add(questionInfo.getStopDate());
	    }
	    
		//删除状态
		hql.append(" and qim.delStatusDic.id = '"+Constants.STATUS_NORMAL.getId()+"'");
		
		//排序条件
		hql.append(" order by createTime desc ");
		
	    Page page = null;
	    
	    if(values.size()>0){
	    	page = this.pagedQuery(hql.toString(), pageNum, pageSize, values.toArray());
	    }else{
	    	page = this.pagedQuery(hql.toString(), pageNum, pageSize);
	    }
		return page;
	}
	
	@Override
	public Page queryUserQuesInfo(int pageNum,int pageSize,String currentUserId) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" select  qnrm.questionNairePo from QuestionNaireRespondentModel qnrm where 1=1");
		/**
		 * 问卷状态
		 */
		hql.append(" and qnrm.questionNairePo.statusDic.id=?");
		values.add(QuestionNaireConstants.PAPER_STATUS_ENABLE.getId());
		
		/**
		 * 问卷类型是迎新的
		 */
		hql.append(" and qnrm.questionNairePo.typeDic.code=?");
		values.add(QuestionNaireConstants.QUESTIONNAIRE_TYPE_ENUMS.QUES_ORIENTATION.toString());
		
		
		/**
		 * 当前用户
		 */
		if(DataUtil.isNotNull(currentUserId)){
			hql.append(" and qnrm.respondent.id=?");
			values.add(currentUserId);
		}
		
		//删除状态
		hql.append(" and qnrm.questionNairePo.delStatusDic.id = '"+Constants.STATUS_NORMAL.getId()+"'");
		
		//排序条件
		hql.append(" order by qnrm.questionNairePo.createTime desc ");
		
	    Page page = null;
	    
	    if(values.size()>0){
	    	page = this.pagedQuery(hql.toString(), pageNum, pageSize, values.toArray());
	    }else{
	    	page = this.pagedQuery(hql.toString(), pageNum, pageSize);
	    }
	    
	    page = this.formatePaperList(page,QuestionNaireConstants.QUESTIONNAIRE_TYPE_ENUMS.QUES_ORIENTATION.toString());
	    
		return page;
	}
	
	/**
	 * 封装【授权问卷+公开问卷】
	 * @param page	分页对象
	 * @return
	 */
	private Page formatePaperList(Page page,String typeCode) {
		List<QuestionInfoModel> paperlist = (ArrayList<QuestionInfoModel>)page.getResult();
		List<QuestionInfoModel>  openPaperList = this.getOpenNaireList(typeCode) ;
		for(QuestionInfoModel qim:openPaperList){
			paperlist.add(qim);
		}
		page.setResult(paperlist);
		return page;
	}

	/**
	 * 获取公开的问卷
	 * @param respondent		答题人
	 * @return
	 */
	public List<QuestionInfoModel> getOpenNaireList(String typeCode) {
		List<Object> vlaues = new ArrayList<Object>();
		StringBuffer hql=new StringBuffer("from QuestionInfoModel qim where 1=1");
		hql.append(" and qim.isOpen=?");
		hql.append(" and qim.statusDic.id=?");
		hql.append(" and qim.delStatusDic.id=?");
		hql.append(" and qim.typeDic.code=?");
		vlaues.add("Y");
		vlaues.add(QuestionNaireConstants.PAPER_STATUS_ENABLE.getId());
		vlaues.add(Constants.STATUS_NORMAL.getId());
		//区分是毕业的还是迎新的
		String code1 = QuestionNaireConstants.QUESTIONNAIRE_TYPE_ENUMS.QUES_ORIENTATION.toString();
		String code2 = QuestionNaireConstants.QUESTIONNAIRE_TYPE_ENUMS.QUES_GRADUATION.toString();
		if(DataUtil.isNotNull(typeCode) && typeCode.equals(code1)){
			vlaues.add(typeCode);
		}else if(DataUtil.isNotNull(typeCode) && typeCode.equals(code2)){
			vlaues.add(typeCode);
		}
		return  this.query(hql.toString(),vlaues.toArray());
	}
	
	@Override
	public Page queryStuQuesInfo(String condition, int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" select respondent from QuestionNaireRespondentModel qnrm where 1=1");
		
		/**
		 * 选择的用户
		 */
		if(DataUtil.isNotNull(condition)){
			hql.append(" and qnrm.respondent.id in ").append(condition);
		}
		
		//答题人类型
		hql.append(" and qnrm.userType in ('"+QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.STUDENT+"',");
		hql.append("'"+QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.NEW_STUDENT+"')");
		
		//删除状态
		hql.append(" and qnrm.questionNairePo.delStatusDic.id = '"+Constants.STATUS_NORMAL.getId()+"'");
		
		//排序条件
		hql.append(" order by qnrm.questionNairePo.createTime desc ");
		
	    Page page = null;
	    
	    if(values.size()>0){
	    	page = this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	    }else{
	    	page = this.pagedQuery(hql.toString(), pageNo, pageSize);
	    }
		return page;
	}
	

	@Override
	public Page queryTeacherQuesInfo(String condition, int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" select respondent from QuestionNaireRespondentModel qnrm where 1=1");
		
		/**
		 * 选择的用户
		 */
		if(DataUtil.isNotNull(condition)){
			hql.append(" and qnrm.respondent.id in ").append(condition);
		}
		
		//答题人类型
		hql.append(" and qnrm.userType = '"+QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.TEACHER+"'");
		
		//删除状态
		hql.append(" and qnrm.questionNairePo.delStatusDic.id = '"+Constants.STATUS_NORMAL.getId()+"'");
		
		//排序条件
		hql.append(" order by qnrm.questionNairePo.createTime desc ");
		
	    Page page = null;
	    
	    if(values.size()>0){
	    	page = this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	    }else{
	    	page = this.pagedQuery(hql.toString(), pageNo, pageSize);
	    }
		return page;
	}
	

	@Override
	public Page getPaperStudent(String paperId, int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" select respondent from QuestionNaireRespondentModel qnrm where 1=1");
		//问卷ID
		hql.append(" and qnrm.questionNairePo.id='"+paperId+"'");
		//答题人类型
		hql.append(" and qnrm.userType in ('"+QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.STUDENT+"',");
		hql.append("'"+QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.NEW_STUDENT+"')");
		//删除状态
		hql.append(" and qnrm.questionNairePo.delStatusDic.id = '"+Constants.STATUS_NORMAL.getId()+"'");
		//排序条件
		hql.append(" order by qnrm.questionNairePo.createTime desc ");
		
	    Page page = null;
	    
	    if(values.size()>0){
	    	page = this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	    }else{
	    	page = this.pagedQuery(hql.toString(), pageNo, pageSize);
	    }
		return page;
	}
	

	@Override
	public Page getPaperTeacher(String paperId, int pageNo, int pageSize) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer(" select respondent from QuestionNaireRespondentModel qnrm where 1=1");
		//问卷ID
		hql.append(" and qnrm.questionNairePo.id='"+paperId+"'");
		//答题人类型
		hql.append(" and qnrm.userType = '"+QuestionNaireConstants.RESPONDENT_TYPE_ENUMS.TEACHER+"'");
		//删除状态
		hql.append(" and qnrm.questionNairePo.delStatusDic.id = '"+Constants.STATUS_NORMAL.getId()+"'");
		//排序条件
		hql.append(" order by qnrm.questionNairePo.createTime desc ");
	    Page page = null;
	    if(values.size()>0){
	    	page = this.pagedQuery(hql.toString(), pageNo, pageSize, values.toArray());
	    }else{
	    	page = this.pagedQuery(hql.toString(), pageNo, pageSize);
	    }
		return page;
	}
	
	@Override
	public QuestionInfoModel getQuestionNairePo(String questionNaireId) {
		StringBuffer hql=new StringBuffer(" from QuestionInfoModel qim where 1=1");
		List<String> values = new ArrayList<String>();
		if(DataUtil.isNotNull(questionNaireId)){
			hql.append(" and qim.id=? and qim.delStatusDic.id=?");
			values.add(questionNaireId);
			values.add(Constants.STATUS_NORMAL.getId());
			return (QuestionInfoModel) this.queryUnique(hql.toString(), values.toArray());
		}
		return new QuestionInfoModel();
	}

	@Override
	public List<QuestionInfoItemModel> getQuesItemByInfoId(String paperId) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer();
		hql.append(" from QuestionInfoItemModel qiim where 1=1 ");
		hql.append(" and qiim.questionInfo.id=?");
		hql.append(" order by qiim.quesSeqNum asc");
		values.add(paperId);
		List<QuestionInfoItemModel> quesItemList = this.query(hql.toString(), values.toArray());
		return quesItemList;
	}
	
	@Override
	public List<QuestionInfoItemModel> getItemByPaperType(String paperId,String paperType) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer();
		hql.append(" from QuestionInfoItemModel qiim where 1=1 ");
		hql.append(" and qiim.questionInfo.id=?");
		values.add(paperId);
		
		//【公共类型问卷，可查询所有问题】【其他类型问卷，可查询(本身+公共)类型题目】
		String commonType = Constants.QUESINFO_TYPE_COMMON.getId();
		if(!paperType.equals(commonType)){
			hql.append(" and qiim.itemCategory.id in('"+paperType+"','"+commonType+"')");
		}
		
		hql.append(" order by qiim.quesSeqNum asc");
		if(DataUtil.isNotNull(paperId) && DataUtil.isNotNull(paperType)){
			List<QuestionInfoItemModel> quesItemList = this.query(hql.toString(), values.toArray());
			return quesItemList;
		}else{
			return new ArrayList<QuestionInfoItemModel>();
		}
	}
	
	@Override
	public List<QuestionInfoItemModel> getItemByPaperId4Summery(String paperId) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer();
		hql.append(" from QuestionInfoItemModel qiim where 1=1 ");
		hql.append(" and qiim.questionInfo.id=?");
		hql.append(" order by qiim.quesSeqNum asc");
		values.add(paperId);
		List<QuestionInfoItemModel> quesItemList = this.query(hql.toString(), values.toArray());
		return quesItemList;
	}
	
	@Override
	public QuestionInfoItemModel getQuesInfoItemByPK(String pk) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer(" from QuestionInfoItemModel qiim where qiim.id=?");
		values.add(pk);
		return (QuestionInfoItemModel)this.queryUnique(hql.toString(), values.toArray());
	}
	
	@Override
	public QuestionInfoItemModel querySinglePaperItem(String paperId,String itemId) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer(" from QuestionInfoItemModel qiim where qiim.questionInfo.id=? and qiim.questionItem.id=?");
		values.add(paperId);
		values.add(itemId);
		return (QuestionInfoItemModel)this.queryUnique(hql.toString(), values.toArray());
	}

	@Override
	public List<QuestionInfoModel> getQuesInfoByInfoName(String name) {
		StringBuffer hql = new StringBuffer(" from QuestionInfoModel qim where qim.name=?");
		List<QuestionInfoModel> questionInfoList = this.query(hql.toString(), new Object[]{name});
		return questionInfoList;
	}

	@Override
	public void deletePaperItem(String paperId, String itemId) {
		StringBuffer hql = new StringBuffer(" delete from QuestionInfoItemModel qiim where qiim.questionInfo.id=?  and qiim.questionItem.id=?");
		this.executeHql(hql.toString(), new Object[]{paperId,itemId});
	}
	
	@Override
	public void deletePaperById(String paperId) {
		StringBuffer hql = new StringBuffer(" delete from QuestionInfoModel qim where qim.id=?");
		this.executeHql(hql.toString(), new Object[]{paperId});
	}

	@Override
	public void deletePaperItem(String paperId) {
		StringBuffer hql = new StringBuffer(" delete from QuestionInfoItemModel qiim where qiim.questionInfo.id=?");
		this.executeHql(hql.toString(), new Object[]{paperId});
	}
	
	@Override
	public void deleteItemOption(String itemId) {
		StringBuffer hql = new StringBuffer(" delete from QuestionItemOptionModel qiom where qiom.paperItemId=?");
		this.executeHql(hql.toString(), new Object[]{itemId});
	}

	@Override
	public void abandonPaper(String paperId) {
		StringBuffer hql = new StringBuffer(" update QuestionInfoModel qim  set  qim.delStatusDic.id=?  where qim.id=?");
		this.executeHql(hql.toString(), new Object[]{Constants.STATUS_DELETED.getId(),paperId});
	}

	@Override
	public void abandonPaperItem(String paperId) {
		StringBuffer hql = new StringBuffer(" update QuestionInfoItemModel qiim  set  qiim.delStatusDic.id=?  where qiim.questionInfo.id=?");
		this.executeHql(hql.toString(), new Object[]{Constants.STATUS_DELETED.getId(),paperId});
	}
	
	@Override
	public void abandonItemOption(String itemId) {
		StringBuffer hql = new StringBuffer(" update QuestionItemOptionModel qiom  set qiom.delStatusDic.id=?  where qiom.paperItemId=?");
		this.executeHql(hql.toString(), new Object[]{Constants.STATUS_DELETED.getId(),itemId});
	}

	@Override
	public QuestionItemOptionModel getItemOption(String paperItemId, String itemOptionId) {
		StringBuffer hql = new StringBuffer(" from QuestionItemOptionModel qiom where 1=1");
		if(DataUtil.isNotNull(paperItemId) && DataUtil.isNotNull(itemOptionId)){
			hql.append(" and qiom.paperItemId=?");
			hql.append(" and qiom.itemOption.id=?");
			return (QuestionItemOptionModel)this.queryUnique(hql.toString(), new Object[]{paperItemId,itemOptionId});
		}
		
		return new QuestionItemOptionModel();
	}

	@Override
	public List<QuestionItemOptionModel> getItemOptioinList(QuestionInfoItemModel qiim) {
		StringBuffer hql = new StringBuffer();
		if(DataUtil.isNotNull(qiim)){
			hql.append(" from QuestionItemOptionModel qiom where 1=1");
			hql.append(" and qiom.questionNaire.id=?");
			hql.append(" and qiom.paperItemId=?");
			hql.append(" order by qiom.seqnum asc");
		}
		return this.query(hql.toString(), new Object[]{qiim.getQuestionInfo().getId(),qiim.getId()});
	}

	@Override
	public void deleteQnrmInfo(String paperId,String userType) {
		List<String> values = new ArrayList<String>();
		StringBuffer hql = new StringBuffer();
		
		if(DataUtil.isNotNull(paperId)){
			hql.append(" delete from QuestionNaireRespondentModel qnrm where qnrm.questionNairePo.id=?");
			values.add(paperId);
		}
		
		if(DataUtil.isNotNull(userType)){
			hql.append(" and qnrm.userType=?");
			values.add(userType);
		}
		
		this.executeHql(hql.toString(), values.toArray());
	}

}
