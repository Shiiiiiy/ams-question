package com.uws.question.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionItemModel;
import com.uws.question.dao.IQuesItemDao;
import com.uws.question.service.IQuesItemService;

@Service("quesItemService")
public class QuesItemServiceImpl implements IQuesItemService {

	@Autowired
	private IQuesItemDao quesItemDao;

	@Override
	public Page queryQuesItem(Integer pageSize, Integer pageNo,
			QuestionItemModel quesItem) {
		return quesItemDao.queryQuesItem(pageSize, pageNo, quesItem);
	}

	@Override
	public void saveQuesItem(QuestionItemModel quesItem) {
		quesItemDao.save(quesItem);
	}

	@Override
	public void updateQuesItem(QuestionItemModel quesItem) {
		quesItemDao.update(quesItem);
	}

	@Override
	public QuestionItemModel findItemById(String id) {
		return (QuestionItemModel) quesItemDao.get(QuestionItemModel.class, id);
	}

	@Override
	public void delItemById(String id) {

		quesItemDao.deleteById(QuestionItemModel.class, id);
	}

	@Override
	public List<QuestionItemModel> queryQuesItemByName(String itemName,
			String id, String itemCategoryId, String itemTypeId) {
		return quesItemDao.queryQuesItemByName(itemName, id, itemCategoryId,
				itemTypeId);
	}

	@Override
	public Page queryItemCompList(int i, int pageNo, String itemName,
			String itemTypeId, String itemCategoryId, String paperType) {
		return quesItemDao.queryItemCompList(i, pageNo, itemName, itemTypeId,
				itemCategoryId, paperType);
	}

	@Override
	public QuestionItemModel queryItemById(String itemId) {
		return quesItemDao.queryItemById(itemId);
	}

}
