package com.uws.question.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.common.service.IQuestionNaireService;
import com.uws.common.util.QuestionNaireConstants;
import com.uws.common.util.QuestionNaireUtil;
import com.uws.core.base.BaseController;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.question.QuestionAnswerBaseModel;
import com.uws.domain.question.QuestionAnswerDetailModel;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionInfoModel;
import com.uws.domain.question.QuestionItemOptionModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.question.service.IQuesInfoService;
import com.uws.question.service.IQuesWriteService;
import com.uws.question.util.Constants;
import com.uws.sys.model.Dic;

/**
 * 问卷回答Controller
 */
@Controller
public class QuesWriteController extends BaseController {
	
	@Autowired
	private IQuesInfoService quesInfoService;
	
	@Autowired
	private IQuestionNaireService questionNaireService;
	
	@Autowired
	private IQuesWriteService quesWriteService;
	
	SessionUtil sessionUtil = SessionFactory.getSession(Constants.QUESTION_MANAGE);
	
	private Logger logger = new LoggerFactory(IQuesWriteService.class);
	
	/**
	 * 选择启用的问卷
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型
	 * @param questionNairePo		问卷对象
	 * @return											问卷选择视图
	 */
	@RequestMapping({"/question/quesSelect/nsm/quesSelectQuery"})
	public String quesSelectQuery(HttpServletRequest request,HttpServletResponse response,
			ModelMap model,QuestionInfoModel questionNairePo){
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		List<Dic> typeList = Constants.paperTypeList;//问卷类型
		List<Dic> paperStatusList = Constants.paperStatusList;//问卷状态
		questionNairePo.setStatusDic(Constants.PAPER_STATUS_ENABLE);
		Page page = this.quesInfoService.queryUserQuesInfo(pageNo, Page.DEFAULT_PAGE_SIZE,this.sessionUtil.getCurrentUserId());
		model.addAttribute("page", page);
		model.addAttribute("questionInfo", questionNairePo);
		model.addAttribute("typeList", typeList);
		model.addAttribute("paperStatusList", paperStatusList);
		model.addAttribute("curpageNo", pageNo);
		model.addAttribute("paperSize", page.getTotalCount());
		return "/question/quesWrite/paperSelectList";
	}
	
	/**
	 *  初始化我的问卷	
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型
	 * @param qabm								答卷对象
	 */
	@RequestMapping(value={"/question/quesWrite/opt-edit/initSelectedPaper"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String initSelectedPaper(HttpServletRequest request,HttpServletResponse response,
			ModelMap model,String paperId,String paperSize){
		String returnValue="";
		if(DataUtil.isNotNull(paperId)){
			QuestionInfoModel questionNairePo = this.quesInfoService.getQuesInfoById(paperId);
			QuestionAnswerBaseModel qabm = 
					this.quesWriteService.getQuesNaireBaseModel(this.sessionUtil.getCurrentUserId(), questionNairePo.getId());
			if(DataUtil.isNotNull(qabm) || (DataUtil.isNotNull(qabm) && DataUtil.isNotNull(qabm.getId()))){
				returnValue = "duplicate";
			}else{
				try {
					qabm = QuestionNaireUtil.formateQuestionNaireBaseInfo(questionNairePo,this.sessionUtil.getCurrentUserId());
					this.quesWriteService.initQuestionNaireBaseInfo(qabm);
					returnValue = "success";
				} catch (Exception e) {
					logger.error(e.getMessage());
					returnValue = "error";
				}
			}
		}else{
			int paperListSize =(DataUtil.isNotNull(paperSize))?Integer.parseInt(paperSize):0;
			if(paperListSize==0){
				returnValue = "null";
			}
		}
		
		return returnValue;
	}
	
	/**
	 *  初始化我的问卷	
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型
	 * @param qabm								答卷对象
	 */
	@RequestMapping(value={"/question/quesWrite/opt-delete/deleteAnswerPaper"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String deleteAnswerPaper(HttpServletRequest request,HttpServletResponse response,
			ModelMap model,String answerPaperId){
		String returnValue="";
		if(DataUtil.isNotNull(answerPaperId)){
				try {
							this.quesWriteService.deleteAnswerPaper(answerPaperId);
							returnValue = "success";
				} catch (Exception e) {
					logger.error(e.getMessage());
					returnValue = "error";
				}
		}
		
		return returnValue;
	}
	
	/**
	 *  查询我的问卷	
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型
	 * @param qabm								答卷对象
	 * @return											答卷列表视图
	 */
	@RequestMapping({"/question/quesWrite/opt-query/paperSelectedQuery"})
	public String paperSelectedQuery(HttpServletRequest request,HttpServletResponse response,
			ModelMap model,QuestionAnswerBaseModel qabm){
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		List<Dic> typeList = Constants.paperTypeList;//问卷类型
		List<Dic> paperStatusList = Constants.paperStatusList;//问卷状态
		List<Dic> answerStatusList = Constants.answerStatusList;//答题状态
		Page page = this.quesWriteService.paperSelectedQuery(qabm, pageNo, Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("qabm", qabm);
		model.addAttribute("questionInfo", new QuestionInfoModel());//供页面回显调用
		model.addAttribute("typeList", typeList);
		model.addAttribute("answerStatusList", answerStatusList);
		model.addAttribute("paperStatusList", paperStatusList);
		return "/question/quesWrite/paperSelectedList";
	}
	
	/**
	 *  异步加载我的问卷	
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型
	 * @param qabm								答卷对象
	 * @return											答卷列表视图
	 */
	@RequestMapping({"/question/quesWrite/nsm/nsmPaperSelectedQuery"})
	public String nsmPaperSelectedQuery(HttpServletRequest request,HttpServletResponse response,
			ModelMap model,QuestionAnswerBaseModel qabm){
		
		int pageNo = request.getParameter("pageNo")!=null?Integer.parseInt(request.getParameter("pageNo")):1;
		List<Dic> typeList = Constants.paperTypeList;//问卷类型
		List<Dic> paperStatusList = Constants.paperStatusList;//问卷状态
		List<Dic> answerStatusList = Constants.answerStatusList;//答题状态
		Page page = this.quesWriteService.paperSelectedQuery(qabm, pageNo, Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		model.addAttribute("qabm", qabm);
		model.addAttribute("questionInfo", new QuestionInfoModel());//供页面回显调用
		model.addAttribute("typeList", typeList);
		model.addAttribute("answerStatusList", answerStatusList);
		model.addAttribute("paperStatusList", paperStatusList);
		return "/question/quesWrite/paperSelectedList";
	}

	/**
	 * 回答当前问卷				
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型		
	 * @param paperId						问卷id	
	 * @return											答题视图	
	 */
	@RequestMapping({"/question/quesWrite/opt-edit/paperEdit"})
	public String editCurPaper(HttpServletRequest request,HttpServletResponse response,ModelMap model,String paperId){
		
		QuestionInfoModel qim = this.questionNaireService.getQuestionNaireInfo(paperId);
		List<QuestionInfoItemModel> singleItemList = null;
		List<QuestionInfoItemModel> mulItemList = null;
		List<QuestionInfoItemModel> subItemList = null;
		List<QuestionItemOptionModel>  singleQuesOptionList = null;
		List<QuestionItemOptionModel>  mulQuesOptionList  = null;
		List<QuestionItemOptionModel>  subQuesAnswerList = null; 
		if(DataUtil.isNotNull(qim) && DataUtil.isNotNull(qim.getId())){
			//获取问卷主键
			String questionNaireId = qim.getId();
			//获取问卷单选题列表
			singleItemList = this.questionNaireService.getQuestionNaireSingleItemList(questionNaireId);
			//获取问卷多选题列表
			mulItemList = this.questionNaireService.getQuestionNaireMulItemList(questionNaireId);
			//获取问卷问答题列表
			subItemList = this.questionNaireService.getQuestionNaireSubItemList(questionNaireId);
			//获取单选题答题列表
			singleQuesOptionList = this.questionNaireService.getSingleQuestionOption(this.sessionUtil.getCurrentUserId(), questionNaireId);
			//获取多选题答题列表
			mulQuesOptionList = this.questionNaireService.getMulQuestionOption(this.sessionUtil.getCurrentUserId(), questionNaireId);
			//获取问答题答题列表
			subQuesAnswerList = this.questionNaireService.getAnswerQuestionOption(this.sessionUtil.getCurrentUserId(), questionNaireId);
		}
		
		model.addAttribute("qim", qim);
		model.addAttribute("singleItemList", singleItemList);
		model.addAttribute("mulItemList", mulItemList);
		model.addAttribute("subItemList", subItemList);
		model.addAttribute("singleQuesOptionList", singleQuesOptionList);
		model.addAttribute("mulQuesOptionList", mulQuesOptionList);
		model.addAttribute("subQuesAnswerList", subQuesAnswerList);
		model.addAttribute("splitFlag", QuestionNaireConstants.AMS_SPLIT_FLAG_QUESTIONNAIRE);
		return "/question/quesWrite/paperEdit";
	}
	
	
	/**
	 * 查看当前问卷				
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型		
	 * @param paperId						问卷id	
	 * @return											答题视图	
	 */
	@RequestMapping({"/question/quesWrite/opt-edit/viewCurPaper"})
	public String viewCurPaper(HttpServletRequest request,HttpServletResponse response,ModelMap model,String paperId){
		
		QuestionInfoModel qim = this.questionNaireService.getQuestionNaireInfo(paperId);
		List<QuestionInfoItemModel> singleItemList = null;
		List<QuestionInfoItemModel> mulItemList = null;
		List<QuestionInfoItemModel> subItemList = null;
		List<QuestionItemOptionModel>  singleQuesOptionList = null;
		List<QuestionItemOptionModel>  mulQuesOptionList  = null;
		List<QuestionItemOptionModel>  subQuesAnswerList = null; 
		if(DataUtil.isNotNull(qim) && DataUtil.isNotNull(qim.getId())){
			//获取问卷主键
			String questionNaireId = qim.getId();
			//获取问卷单选题列表
			singleItemList = this.questionNaireService.getQuestionNaireSingleItemList(questionNaireId);
			//获取问卷多选题列表
			mulItemList = this.questionNaireService.getQuestionNaireMulItemList(questionNaireId);
			//获取问卷问答题列表
			subItemList = this.questionNaireService.getQuestionNaireSubItemList(questionNaireId);
			//获取单选题答题列表
			singleQuesOptionList = this.questionNaireService.getSingleQuestionOption(this.sessionUtil.getCurrentUserId(), questionNaireId);
			//获取多选题答题列表
			mulQuesOptionList = this.questionNaireService.getMulQuestionOption(this.sessionUtil.getCurrentUserId(), questionNaireId);
			//获取问答题答题列表
			subQuesAnswerList = this.questionNaireService.getAnswerQuestionOption(this.sessionUtil.getCurrentUserId(), questionNaireId);
		}
		
		model.addAttribute("qim", qim);
		model.addAttribute("singleItemList", singleItemList);
		model.addAttribute("mulItemList", mulItemList);
		model.addAttribute("subItemList", subItemList);
		model.addAttribute("singleQuesOptionList", singleQuesOptionList);
		model.addAttribute("mulQuesOptionList", mulQuesOptionList);
		model.addAttribute("subQuesAnswerList", subQuesAnswerList);
		model.addAttribute("splitFlag", QuestionNaireConstants.AMS_SPLIT_FLAG_QUESTIONNAIRE);
		return "/question/quesWrite/paperView";
	}
	
	/**
	 * 保存当前问卷
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型
	 * @param questionNairePo		问卷对象
	 * @param singleOption				单选选中选项
	 * @param mulOption					多选选中选项
	 * @param singleQadms				单选题答案数组
	 * @param mulQadms					多选题答案数组
	 * @param answerQadms			问答题目信息数组
	 * @param subItemAreas				问答题答案数组
	 * @return											答题列表
	 */
	@RequestMapping({"/question/quesWrite/opt-edit/saveCurPaper"})
	public String saveCurPaper(HttpServletRequest request,HttpServletResponse response,
			ModelMap model,QuestionInfoModel questionNairePo,
			String [] singleOption,String [] mulOption,
			String [] singleQadms,String [] mulQadms,String [] answerQadms,String [] subItemAreas){
		
		//当前用户ID
		String userId = this.sessionUtil.getCurrentUserId();
		//答卷基本信息
		QuestionAnswerBaseModel qabm = this.getQuestionNaireBaseInfo(questionNairePo);
		//答卷单选题信息
		List<QuestionAnswerDetailModel> singleQadmList = QuestionNaireUtil.getSingleQuestionNaireDetailInfo(singleOption,singleQadms,qabm,userId);
		//答卷多选题信息
		List<QuestionAnswerDetailModel> mulQadmList = QuestionNaireUtil.getMulQuestionNaireDetailInfo(mulOption,mulQadms,qabm,userId);
		//答卷问答题信息
		List<QuestionAnswerDetailModel> answerQadmList = QuestionNaireUtil.getAnswerQuestionNaireDetailInfo(answerQadms,qabm,subItemAreas,userId);
		//保存当前问卷信息
		this.questionNaireService.saveCurQuestionNaire(qabm, singleQadmList, mulQadmList, answerQadmList);
		
		return "redirect:/question/quesWrite/opt-query/paperSelectedQuery.do";
	}

	/**
	 * 提交当前问卷	
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型
	 * @param questionNairePo		问卷对象
	 * @param singleOption				单选选中选项
	 * @param mulOption					多选选中选项
	 * @param singleQadms				单选题答案数组
	 * @param mulQadms					多选题答案数组
	 * @param answerQadms			问答题目信息数组
	 * @param subItemAreas				问答题答案数组
	 * @return											答题列表
	 */
	@RequestMapping({"/question/quesWrite/opt-edit/submitCurPaper"})
	public String submitCurPaper(HttpServletRequest request,HttpServletResponse response,
			ModelMap model,QuestionInfoModel questionNairePo,
			String [] singleOption,String [] mulOption,
			String [] singleQadms,String [] mulQadms,String [] answerQadms,String [] subItemAreas){
		
		//当前用户ID
		String userId = this.sessionUtil.getCurrentUserId();
		//答卷基本信息
		QuestionAnswerBaseModel qabm = this.getQuestionNaireBaseInfo(questionNairePo);
		//答卷单选题信息
		List<QuestionAnswerDetailModel> singleQadmList = QuestionNaireUtil.getSingleQuestionNaireDetailInfo(singleOption,singleQadms,qabm,userId);
		//答卷多选题信息
		List<QuestionAnswerDetailModel> mulQadmList = QuestionNaireUtil.getMulQuestionNaireDetailInfo(mulOption,mulQadms,qabm,userId);
		//答卷问答题信息
		List<QuestionAnswerDetailModel> answerQadmList = QuestionNaireUtil.getAnswerQuestionNaireDetailInfo(answerQadms,qabm,subItemAreas,userId);
		//提交当前问卷信息
		this.questionNaireService.submitCurQuestionNaire(qabm, singleQadmList, mulQadmList, answerQadmList);
		
		return "redirect:/question/quesWrite/opt-query/paperSelectedQuery.do";
	}
	
	/**
	 * 取消当前问卷
	 * @param request							当次请求
	 * @param response						当次响应
	 * @param model							页面模型
	 * @param questionNairePo		问卷对象
	 * @return											答题列表
	 */
	@RequestMapping({"/question/quesWrite/opt-edit/cancelCurPaper"})
	public String cancelCurPaper(HttpServletRequest request,HttpServletResponse response,
			ModelMap model,QuestionInfoModel questionNairePo){
		
		return "redirect:/question/quesWrite/opt-query/paperSelectedQuery.do";
	}
	
	/**
	 * 获取答卷的基本信息
	 * @param questionNairePo	问卷对象
	 * @return										答卷基本信息
	 */
	private QuestionAnswerBaseModel getQuestionNaireBaseInfo(QuestionInfoModel questionNairePo) {

		String userId = this.sessionUtil.getCurrentUserId();
		QuestionAnswerBaseModel qabm = 
				this.questionNaireService.getQuesNaireBaseModel(userId,questionNairePo.getQuestionNaireId());
		if(!DataUtil.isNotNull(qabm)){
			qabm = QuestionNaireUtil.formateQuestionNaireBaseInfo(questionNairePo,userId);
		}
		return qabm;
	}
	
}
