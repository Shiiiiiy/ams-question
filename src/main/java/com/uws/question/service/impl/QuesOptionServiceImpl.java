package com.uws.question.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uws.domain.question.QuestionOptionModel;
import com.uws.question.dao.IQuesOptionDao;
import com.uws.question.service.IQuesOptionService;

@Service("quesOptionService")
public class QuesOptionServiceImpl implements IQuesOptionService{

	@Autowired
	private IQuesOptionDao quesOptionDao;

	@Override
	public List<QuestionOptionModel> queryOptionByItemId(String itemId) {
		return quesOptionDao.queryOptionByItemId(itemId);
	}

	@Override
	public void deleteOptionByItemId(String itemId) {
		quesOptionDao.deleteOptionByItemId(itemId);		
	}

	@Override
	public void saveOption(QuestionOptionModel option) {
		quesOptionDao.save(option);		
	}

	@Override
	public void updateOption(QuestionOptionModel option) {
		quesOptionDao.update(option);		
	}

	@Override
	public QuestionOptionModel findOptionById(String id) {
		return (QuestionOptionModel) quesOptionDao.get(QuestionOptionModel.class, id);
	}

	@Override
	public void deleteOptionById(String id) {
		quesOptionDao.deleteById(QuestionOptionModel.class, id);
	}

	@Override
	public List<QuestionOptionModel> queryOptionByNameAndItemId(String itemId,
			String optionName) {
		return quesOptionDao.queryOptionByNameAndItemId(itemId, optionName);
	}
}
