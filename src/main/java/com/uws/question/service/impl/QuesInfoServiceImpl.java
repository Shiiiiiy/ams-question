package com.uws.question.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.common.util.ConvertUtils;
import com.uws.core.base.BaseModel;
import com.uws.core.base.BaseServiceImpl;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionItemOptionModel;
import com.uws.domain.question.QuestionOptionModel;
import com.uws.question.dao.IQuesInfoDao;
import com.uws.question.dao.IQuesOptionDao;
import com.uws.question.service.IQuesInfoService;
import com.uws.question.util.Constants;

/**
 *	调查问卷维护service实现
 */
@Service("quesInfoService")
public class QuesInfoServiceImpl extends BaseServiceImpl implements IQuesInfoService {

	@Autowired
	private IQuesInfoDao quesInfoDao;
	
	@Autowired
	private IQuesOptionDao quesOptionDao;
	
	@Override
	public Page queryQuesInfo(QuestionInfoModel questionInfo,int pageNum,int pageSize) {
		return this.quesInfoDao.queryQuesInfo(questionInfo,pageNum,pageSize);
	}
	
	@Override
	public Page queryUserQuesInfo(int pageNum,int pageSize,String currentUserId) {
		return this.quesInfoDao.queryUserQuesInfo(pageNum,pageSize,currentUserId);
	}
	
	@Override
	public Page queryStuQuesInfo(String condition, int pageNo, int pageSize) {
		return this.quesInfoDao.queryStuQuesInfo(condition,pageNo,pageSize);
	}

	@Override
	public Page queryTeacherQuesInfo(String condition, int pageNo,int pageSize) {
		return this.quesInfoDao.queryTeacherQuesInfo(condition,pageNo,pageSize);
	}
	
	@Override
	public Page getPaperStudent(String paperId, int pageNo, int pageSize) {
		return this.quesInfoDao.getPaperStudent(paperId,pageNo,pageSize);
	}
	
	@Override
	public Page getPaperTeacher(String paperId, int pageNo, int pageSize) {
		return this.quesInfoDao.getPaperTeacher(paperId,pageNo,pageSize);
	}

	@Override
	public QuestionInfoModel getQuesInfoById(String id) {		
		return (QuestionInfoModel) this.quesInfoDao.get(QuestionInfoModel.class, id);
	}
	
	@Override
	public QuestionInfoModel getQuestionNairePo(String questionNaireId) {
		return this.quesInfoDao.getQuestionNairePo(questionNaireId);
	}

	@Override
	public List<QuestionInfoItemModel> getQuesItemByInfoId(String id) {
		return this.quesInfoDao.getQuesItemByInfoId(id);
	}
	
	@Override
	public List<QuestionInfoItemModel> getItemByPaperId4Summery(String paperId){
		return this.quesInfoDao.getItemByPaperId4Summery(paperId);
	}

	@Override
	public QuestionInfoItemModel getQuesInfoItemByPK(String pk) {
		return this.quesInfoDao.getQuesInfoItemByPK(pk);
	}
	
	@Override
	public QuestionInfoItemModel querySinglePaperItem(String paperId,String itemId) {
		return this.quesInfoDao.querySinglePaperItem(paperId,itemId);
	}

	@Override
	public List<QuestionInfoModel> getQuesInfoByInfoName(String name) {
		return this.quesInfoDao.getQuesInfoByInfoName(name);
	}

	@Override
	public void deletePaperById(String paperId) {
		this.quesInfoDao.deletePaperById(paperId);
	}

	@Override
	public void saveQuesInfo(BaseModel qim) {
		this.quesInfoDao.save(qim);
	}
	
	@Override
	public void updateQuesInfo(QuestionInfoModel quesInfoModel) {
		this.quesInfoDao.update(quesInfoModel);
	}

	@Override
	public void savePaperItem(QuestionInfoItemModel qiim) {
			//保存问卷-问题关系表
			this.quesInfoDao.save(qiim);
			//级联保存题目的选项
			this.saveItemOptionCascade(qiim);
	}

	/**
	 * 级联保存题目选项
	 * @param qiim
	 */
	private void saveItemOptionCascade(QuestionInfoItemModel qiim) {
		if(DataUtil.isNotNull(qiim)&&DataUtil.isNotNull(qiim.getQuestionItem())){
			String itemType = qiim.getItemType().getCode();
			if(itemType.equals(Constants.ITEMTYPE_ESSAY.getCode())){
				QuestionItemOptionModel qiom = this.formateQiomInfo(qiim,null);
				this.quesInfoDao.save(qiom);
			}else{
				String itemId = qiim.getQuestionItem().getId();
				List<QuestionOptionModel>  qomlist = quesOptionDao.queryOptionByItemId(itemId);
				for(QuestionOptionModel qom:qomlist){
					String paperItemId = qiim.getId();
					QuestionItemOptionModel existQiom = this.quesInfoDao.getItemOption(paperItemId,qom.getId());
					if(!DataUtil.isNotNull(existQiom)){//只新增题目中不存在的选项
						QuestionItemOptionModel qiom = this.formateQiomInfo(qiim,qom);
						this.quesInfoDao.save(qiom);
					}
				}
			}
		}
	}

	/**
	 * 封装问题-关系表
	 * @param qiim		问卷-问题关系对象
	 * @param qom		问题选项对象
	 * @return					问题-选项关系对象
	 */
	private QuestionItemOptionModel formateQiomInfo(QuestionInfoItemModel qiim,QuestionOptionModel qom) {
		QuestionItemOptionModel qiom = new QuestionItemOptionModel();
		//设置问卷
		qiom.setQuestionNaire(qiim.getQuestionInfo());
		//设置问卷问题关系表主键
		qiom.setPaperItemId(qiim.getId());
		//设置问题名称
		qiom.setItemName(qiim.getItemName());
		//设置问题序号
		qiom.setItemSeq(qiim.getQuesSeqNum());
		//设置题型序号
		qiom.setItemTypeSeq(qiim.getItemTypeSeq());
		//设置问题类型
		qiom.setItemType(qiim.getItemType());
		if(DataUtil.isNotNull(qom)){//设置选择题的选项
			//设置选项
			qiom.setItemOption(qom);
			//设置选项名称
			qiom.setOptionName(qom.getOptionName());
			// 设置选项编号
			String seqNum = String.valueOf(qom.getSeqNum());
			qiom.setOptionCode(ConvertUtils.num2UpperLetter(seqNum));
			//设置选项顺序
			qiom.setSeqnum(qom.getSeqNum());
			//设置选项超链接
			qiom.setOptionUrl(qom.getOptionUrl());
		}
		//设置选项删除状态
		qiom.setDelStatusDic(Constants.STATUS_NORMAL);
		
		return qiom;
	}

	@Override
	public void abandonPaper(String paperId) {
		this.quesInfoDao.abandonPaper(paperId);
	}
	
	@Override
	public void deletePaperItem(String paperId, String itemId) {
		this.quesInfoDao.deletePaperItem(paperId,itemId);
	}

	@Override
	public void deletePaperItem(String paperId) {
		//先删除问题下的选项
		List<QuestionInfoItemModel>  qiimList = this.quesInfoDao.getQuesItemByInfoId(paperId);
		for(QuestionInfoItemModel qiim:qiimList){
			if(DataUtil.isNotNull(qiim) && DataUtil.isNotNull(qiim.getQuestionItem())){
				this.quesInfoDao.deleteItemOption(qiim.getId());
			}
		}
		//再删除问卷下的问题
		this.quesInfoDao.deletePaperItem(paperId);
		
		//再删除问卷
		this.deletePaperById(paperId);
	}

	@Override
	public void deletePaperItemOnly(String paperId) {
		//先删除问题下的选项
		List<QuestionInfoItemModel>  qiimList = this.quesInfoDao.getQuesItemByInfoId(paperId);
		for(QuestionInfoItemModel qiim:qiimList){
			if(DataUtil.isNotNull(qiim) && DataUtil.isNotNull(qiim.getQuestionItem())){
				this.quesInfoDao.deleteItemOption(qiim.getId());
			}
		}
		//再删除问卷下的问题
		this.quesInfoDao.deletePaperItem(paperId);
	}

	@Override
	public void abandonPaperItem(String paperId) {
		//先废弃问题下的选项
		List<QuestionInfoItemModel>  qiimList = this.quesInfoDao.getQuesItemByInfoId(paperId);
		for(QuestionInfoItemModel qiim:qiimList){
			if(DataUtil.isNotNull(qiim) && DataUtil.isNotNull(qiim.getQuestionItem())){
				this.quesInfoDao.abandonItemOption(qiim.getId());
			}
		}
		//再废弃问卷下的问题
		this.quesInfoDao.abandonPaperItem(paperId);
		
		//再废弃问卷
		this.abandonPaper(paperId);
	}

	@Override
	public void deleteItemOption(String itemId) {
		if(DataUtil.isNotNull(itemId)){
			this.quesInfoDao.deleteItemOption(itemId);
		}
	}

	@Override
	public List<QuestionItemOptionModel> getItemOptioinList(QuestionInfoItemModel qiim) {
		return quesInfoDao.getItemOptioinList(qiim);
	}

	@Override
	public List<QuestionInfoItemModel> getItemByPaperType(String paperId,String paperType) {
		return this.quesInfoDao.getItemByPaperType(paperId, paperType);
	}

	@Override
	public void saveBm(BaseModel bm) {
		this.quesInfoDao.save(bm);
	}

	@Override
	public void deleteQnrmInfo(String paperId,String userType) {
		this.quesInfoDao.deleteQnrmInfo(paperId,userType);
	}

}
