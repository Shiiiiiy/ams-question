package com.uws.question.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.question.util.Constants;
import com.uws.common.service.IRankService;
import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.session.SessionFactory;
import com.uws.core.session.SessionUtil;
import com.uws.core.util.DataUtil;
import com.uws.domain.question.QuestionOptionModel;
import com.uws.domain.question.QuestionItemModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.question.service.IQuesItemService;
import com.uws.question.service.IQuesOptionService;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;
import com.uws.user.model.User;

/**
* @ClassName: QuesItemController 
* @Description: 题目管理模块
 */
@Controller
@RequestMapping(Constants.QUESTION_QUESTIONITEM)
public class QuesItemController {

	// 日志
	private Logger log = new LoggerFactory(QuesItemController.class);
	// sessionUtil工具类
	private SessionUtil sessionUtil = SessionFactory.getSession(Constants.QUESTION_QUESTIONITEM);
	//数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IQuesItemService quesItemService;
	@Autowired
	private IQuesOptionService quesOptionSerivce;
	@Autowired
	private IRankService rankService;
	
	/**
	 * 题目列表信息—分页查询
	 * @param model
	 * @param request
	 * @param quesItem
	 * @return
	 */
	@RequestMapping({"/opt-query/quesItemQuery"})
	public String quesItemQuery(ModelMap model, HttpServletRequest request,QuestionItemModel quesItem){
		log.info("题目维护查询列表");
		Integer pageNo = request.getParameter("pageNo")!=null?Integer.valueOf(request.getParameter("pageNo")):1;
		Page page = quesItemService.queryQuesItem(Page.DEFAULT_PAGE_SIZE, pageNo, quesItem);
		model.addAttribute("page", page);
		model.addAttribute("quesItem", quesItem);
		model.addAttribute("itemTypeList",dicUtil.getDicInfoList("ITME_TYPE"));
		model.addAttribute("useStatusList",dicUtil.getDicInfoList("PAPER_STATUS"));
		model.addAttribute("categoryList",dicUtil.getDicInfoList("QUESINFO_TYPE"));
		model.addAttribute("curpageNo", pageNo);
		return Constants.QUESTION_QUESTIONITEM+"/quesItemList";
	}
	/**
	 * 异步加载—显示题目列表
	 * @param model
	 * @param request
	 * @param quesItem
	 * @return
	 */
	@RequestMapping({"/nsm/quesItemQuery"})
	public String retItemQuery(ModelMap model, HttpServletRequest request,QuestionItemModel quesItem,String pageNumber){
		log.info("异步加载题目列表");
		int pageNo = DataUtil.isNotNull(pageNumber)?Integer.parseInt(pageNumber):1;
		Page page = quesItemService.queryQuesItem(Page.DEFAULT_PAGE_SIZE, pageNo, quesItem);
		model.addAttribute("page", page);
		model.addAttribute("quesItem", quesItem);
		model.addAttribute("itemTypeList",dicUtil.getDicInfoList("ITME_TYPE"));
		model.addAttribute("useStatusList",dicUtil.getDicInfoList("PAPER_STATUS"));
		model.addAttribute("categoryList",dicUtil.getDicInfoList("QUESINFO_TYPE"));
		model.addAttribute("curpageNo", pageNo);
		return Constants.QUESTION_QUESTIONITEM+"/nsmItemList";
	}
	/**
	 * 题目信息—新增/修改操作
	 * @param model
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value={"/opt-add/addQuesItem","/opt-edit/editQuesItem"})
	public String editQuesItem(ModelMap model,HttpServletRequest request,String id)
	{
		log.info("题目维护新增/修改方法");
		QuestionItemModel quesItem = null;
		List<QuestionOptionModel> optionList =new ArrayList<QuestionOptionModel>();
		if(StringUtils.isNotEmpty(id))
		{
			quesItem = quesItemService.findItemById(id);
			optionList = quesOptionSerivce.queryOptionByItemId(id);
		}else{
			quesItem = new QuestionItemModel();
		}
		//答案选项显示
		model.addAttribute("itemTypeEssayId", Constants.ITEMTYPE_ESSAY.getId());
		model.addAttribute("optionList", optionList);
		model.addAttribute("optionSize", optionList.size());
		model.addAttribute("quesItem",quesItem);
		model.addAttribute("itemTypeList",dicUtil.getDicInfoList("ITME_TYPE"));
		model.addAttribute("requiredList",dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("categoryList",dicUtil.getDicInfoList("QUESINFO_TYPE"));
		return Constants.QUESTION_QUESTIONITEM+"/quesItemEdit";
	}
	
	/**
	 * 保存新增或/修改的题目信息
	 * @param model
	 * @param request
	 * @param quesItem
	 * @return
	 */
	@RequestMapping(value={"/opt-add/submitQuesItem","/opt-edit/submitQuesItem"})
	public String submitQuesItem(ModelMap model,HttpServletRequest request,QuestionItemModel quesItem)
	{
		log.info("题目维护-保存方法");
		//判断题目的状态
		String ustatus = request.getParameter("ustatus");
		if(ustatus!=null && ustatus.equals("SAVE")){
			quesItem.setUseStatus(Constants.PAPER_STATUS_SAVED);
		}
		if(ustatus!=null && ustatus.equals("ENA")){
			quesItem.setUseStatus(Constants.PAPER_STATUS_ENABLE);
		}
		if(ustatus!=null && ustatus.equals("DIS")){
			quesItem.setUseStatus(Constants.PAPER_STATUS_DISABLE);
		}		
		quesItem.setCreator(new User(sessionUtil.getCurrentUserId()));
		if(null != quesItem.getId() && !quesItem.getId().equals("")){
			//保存修改信息
			QuestionItemModel qo = quesItemService.findItemById(quesItem.getId());
			BeanUtils.copyProperties(quesItem,qo,new String[]{"createTime","status"});
			quesItemService.updateQuesItem(qo);
		}else{
			//保存新增信息
			quesItem.setStatus(Constants.STATUS_NORMAL);
			quesItemService.saveQuesItem(quesItem);
		}
		//如果是启用状态,就跳转到列表页面
		if(quesItem.getUseStatus().getCode().equals("STATUS_ENABLE")){
			return "redirect:"+Constants.QUESTION_QUESTIONITEM+"/opt-query/quesItemQuery.do";
		}else{
			return "redirect:"+Constants.QUESTION_QUESTIONITEM+"/opt-edit/editQuesItem.do?id="+quesItem.getId();
		}
	}
	
	/**
	 * 删除题目信息（逻辑删除）
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"opt-del/deleteQuesItem"},produces={"text/plain"})
	public String deleteQuesItem(HttpServletRequest request,String id,String ustatusCode)
	{
		if(StringUtils.isNotEmpty(id))
		{
			//保存状态物理删除
			if(ustatusCode!=null && ustatusCode.equals("STATUS_SAVE")){
				quesItemService.delItemById(id);
				//删除题目信息时，级联删除答案选项的信息
				quesOptionSerivce.deleteOptionByItemId(id);
			}
			//禁用状态逻辑删除
			if(ustatusCode!=null && ustatusCode.equals("STATUS_DISABLE")){
				QuestionItemModel quesItem = quesItemService.findItemById(id);
				quesItem.setStatus(Constants.STATUS_DELETED);
				quesItemService.saveQuesItem(quesItem);
			}
			log.info("删除操作成功！");
			return "success";
		}else{
			return "error";
		}
	}
	
	/**
	 * 题目管理—判断题目名称是否重复
	 * @param id
	 * @param itemName
	 * @return
	 */
   @RequestMapping(value={"/opt-query/itemNameCheck"}, produces={"text/plain;charset=UTF-8"})
   @ResponseBody
   public String itemNameCheck(@RequestParam String id, @RequestParam String itemName,
		   @RequestParam String itemCategoryId,@RequestParam String itemTypeId)
   {
	   String b="true";
	   List<QuestionItemModel> itemList = quesItemService.queryQuesItemByName(itemName, id,itemCategoryId,itemTypeId);
	    if ((itemList != null) && (itemList.size() > 0)) {
	    	for(QuestionItemModel item:itemList){
	    		if(!item.getId().equals(id)){
	    			b="";
	    		}
	    	}
	     }
	    return b;
   }
	/**
	 * 判断选项名称是否重复
	 * @param id
	 * @param itemName
	 * @return
	 */
  @RequestMapping(value={"/opt-query/optionNameCheck"}, produces={"text/plain;charset=UTF-8"})
  @ResponseBody
  public String optionNameCheck(@RequestParam String id, @RequestParam String itemId,@RequestParam String optionName)
  {
	   String b="true";
	   List<QuestionOptionModel> optionList = quesOptionSerivce.queryOptionByNameAndItemId(itemId, optionName);
	    if ((optionList != null) && (optionList.size() > 0)) {
	    	for(QuestionOptionModel option:optionList){
	    		if(!option.getId().equals(id)){
	    			b="";
	    		}
	    	}
	     }
	    return b;
  } 
	/**
	 * 删除答案选项信息(逻辑删除)
	 * @param request
	 * @param optionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value={"/opt-del/delQuesOption"},produces={"text/plain"})
	public String deleteQuesOption(HttpServletRequest request,String optionId,String itemId)
	{
		if(StringUtils.isNotEmpty(optionId))
		{
			//删除答案选项的信息
			QuestionOptionModel option = quesOptionSerivce.findOptionById(optionId);
			int delSeq = option.getSeqNum();
			option.setStatus(Constants.STATUS_DELETED);
			quesOptionSerivce.saveOption(option);
			log.info("删除答案选项操作成功！");
			List<QuestionOptionModel> optionList = quesOptionSerivce.queryOptionByItemId(itemId);
			//调整选项顺序
			if(optionList!=null && optionList.size()>0){
				moveSeq(optionList,delSeq);
			}
			return "success";
		}else{
			return "error";
		}
	} 

	/**
	 * 异步加载答案选项
	 */
	   @RequestMapping({"/nsm/optionList"})
	   public String showOptionList(ModelMap model, HttpServletRequest request)
	   {
		   //接收返回的题目id
		   String id=request.getParameter("id");
		   List<QuestionOptionModel> optionList = new ArrayList<QuestionOptionModel>();
			if(StringUtils.isNotEmpty(id))
			{
				optionList = quesOptionSerivce.queryOptionByItemId(id);
			}
		   model.addAttribute("optionList",optionList);	
		   model.addAttribute("optionSize",optionList.size());
		   return Constants.QUESTION_QUESTIONITEM+"/optionList";
	   }
	   
	   /**
	    * 题目选项管理-新增/修改
	    */
	   @RequestMapping({"/nsm/editOption"})
	   public String editOption(ModelMap model, HttpServletRequest request)
	   {
		   String id = request.getParameter("optionId");
		   QuestionOptionModel option = null;
		   if (StringUtils.isNotEmpty(id)) {
			   option = quesOptionSerivce.findOptionById(id);
		   }else{
			   option = new QuestionOptionModel();
		   }
		   model.addAttribute("option",option);	
		   return Constants.QUESTION_QUESTIONITEM+"/optionEdit";
	   }
	 
	 /**
	  * 题目选项管理-新增/修改，提交操作
	  * @param request
	  * @return
	  */
	@RequestMapping(value={"/opt-edit/submitOption"},produces={"text/plain"})
	@ResponseBody
	public String subQuesOption(QuestionOptionModel option, HttpServletRequest request)
	{
	   String itemId = request.getParameter("itemid");
	  
	   if(StringUtils.isNotEmpty(itemId)){
		   QuestionItemModel itemModel = quesItemService.findItemById(itemId);
		   option.setItem(itemModel);
	   }
	   if (StringUtils.isNotEmpty(option.getId())) {
		   	QuestionOptionModel op = quesOptionSerivce.findOptionById(option.getId());
			BeanUtils.copyProperties(option,op,new String[]{"seqNum","status"});
			quesOptionSerivce.updateOption(op);
	   }else{
		   int seq=0;
		  List<QuestionOptionModel> optionList = quesOptionSerivce.queryOptionByItemId(itemId);
		  if(optionList!=null && optionList.size()>0){
			  //获取排序后的题目选项List
			   List<QuestionOptionModel> optList = changeSeq(optionList);
			   seq = optList.get(optList.size()-1).getSeqNum();
		  }
		  option.setSeqNum(seq+1);
		  option.setStatus(Constants.STATUS_NORMAL);
		  quesOptionSerivce.saveOption(option);
	   }
	   return "success";
	}
	
	/**
	 *修改题目的启用状态
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value={"/opt-edit/changeStatus"},produces={"text/plain"})
	@ResponseBody
	public String changeStatus(HttpServletRequest request,String id,String ustatus)
	{
		if(StringUtils.isNotEmpty(id))
		{
			List<QuestionOptionModel> optionList = quesOptionSerivce.queryOptionByItemId(id);
			Dic essay = Constants.ITEMTYPE_ESSAY;
			QuestionItemModel quesItem = quesItemService.findItemById(id);
			if(ustatus!=null && ustatus.equals("DIS")){
				quesItem.setUseStatus(Constants.PAPER_STATUS_DISABLE);
			}
			if(ustatus!=null && ustatus.equals("ENA")){
				//问答类型的题目无需作此判断
				if(optionList!=null && optionList.size()<=0 && !essay.getId().equals(quesItem.getItemType().getId())){
					return "notEnough";
				}				
				quesItem.setUseStatus(Constants.PAPER_STATUS_ENABLE);
			}
			quesItemService.updateQuesItem(quesItem);
			log.info("修改启用状态成功！");
			return "success";
		}else{
			return "error";
		}
	}
	
	/**
	 * 题目信息—查看操作
	 * @param model
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value={"/opt-query/quesItemView"})
	public String quesItemView(ModelMap model,HttpServletRequest request,String id)
	{
		log.info("题目维护查看方法");
		QuestionItemModel quesItem = null;
		List<QuestionOptionModel> optionList =null;
		//答案选项显示
		if(StringUtils.isNotEmpty(id))
		{
			quesItem = quesItemService.findItemById(id);
			optionList = quesOptionSerivce.queryOptionByItemId(id);
		}else{
			quesItem = new QuestionItemModel();
		}
		model.addAttribute("optionList", optionList);
		model.addAttribute("quesItem",quesItem);
		model.addAttribute("itemTypeList",dicUtil.getDicInfoList("ITME_TYPE"));
		model.addAttribute("requiredList",dicUtil.getDicInfoList("Y&N"));
		model.addAttribute("categoryList",dicUtil.getDicInfoList("QUESINFO_TYPE"));
		return Constants.QUESTION_QUESTIONITEM+"/quesItemView";
	}
	
	/**
	 * 上移问题
	 * @param request
	 * @param response
	 * @return
	 * @throws ClassNotFoundException 
	 */
	@RequestMapping(value={"/opt-modify/moveUpItem"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String moveUpItem(HttpServletRequest request,HttpServletResponse response,String id){
		String returnValue="";
		QuestionOptionModel option = this.quesOptionSerivce.findOptionById(id);
		try {
			if(DataUtil.isNotNull(option)){
				String rankColumn="seqNum";//排序字段属性
				int rankValue=option.getSeqNum();//当前对象序号
				String fkColumn="item";//外键对象属性
				String fkValue=option.getItem().getId();//外键值
				boolean  isMinSeq = this.rankService.isMinSeq(option,rankColumn,rankValue,fkColumn,fkValue);
				if(isMinSeq){
					returnValue="min";
				}else{
					this.rankService.moveUpObject(option, rankColumn, rankValue, fkColumn, fkValue);
					returnValue="success";
				}
			}else{
				returnValue="null";
			}
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}
	
	/**
	 * 下移问题
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value={"/opt-modify/moveDownItem"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String moveDownItem(HttpServletRequest request,HttpServletResponse response,String id){
		String returnValue="";
		QuestionOptionModel option = this.quesOptionSerivce.findOptionById(id);
		try {
			if(DataUtil.isNotNull(option)){
				String rankColumn="seqNum";//排序字段属性
				int rankValue=option.getSeqNum();//当前对象序号
				String fkColumn="item";//外键对象属性
				String fkValue=option.getItem().getId();//外键值
				boolean  isMaxSeq = this.rankService.isMaxSeq(option,rankColumn,rankValue,fkColumn,fkValue);
				if(isMaxSeq){
					returnValue="max";
				}else{
					this.rankService.moveDownObject(option,rankColumn,rankValue,fkColumn,fkValue);
					returnValue="success";
				}
			}else{
				returnValue="null";
			}
		} catch (Exception e) {
			returnValue = "error";
		}
		return returnValue;
	}
	
	/**
	 * 选项顺序比较大小
	 * @param optionList
	 * @return
	 */
	private List<QuestionOptionModel> changeSeq(List<QuestionOptionModel> optionList){
		int maxNum=0;
		  for(int i=0;i<optionList.size();i++){
			  for(int j=i+1;j<optionList.size();j++){
				  if(optionList.get(i).getSeqNum()>optionList.get(j).getSeqNum() ){
					  maxNum = optionList.get(i).getSeqNum();
					  optionList.get(i).setSeqNum(optionList.get(j).getSeqNum());
					  optionList.get(j).setSeqNum(maxNum);
				  }
			  }
		  }
	   return optionList;
	}
	
	/**
	 * 根据当前的选项顺序，调整List顺序值
	 * @param optionList
	 * @param delSeq
	 * @return
	 */
   private List<QuestionOptionModel> moveSeq(List<QuestionOptionModel> optionList, int delSeq) {
	   //如果删除的是最后一个选项，就直接返回List
	   if(optionList.size()==delSeq-1){
		   return optionList;
	   }
	   for(int i=0;i<optionList.size();i++){
		   QuestionOptionModel option = optionList.get(i);
		   if(option.getSeqNum()>delSeq){
			   option.setSeqNum(option.getSeqNum()-1);
			   quesOptionSerivce.saveOption(option);
		   }
	   }
		
		return optionList;
	}

}
