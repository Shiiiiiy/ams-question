package com.uws.question.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uws.core.hibernate.dao.support.Page;
import com.uws.core.util.DataUtil;
import com.uws.domain.question.QuestionInfoItemModel;
import com.uws.domain.question.QuestionItemModel;
import com.uws.log.Logger;
import com.uws.log.LoggerFactory;
import com.uws.question.service.IQuesInfoService;
import com.uws.question.service.IQuesItemService;
import com.uws.question.util.Constants;
import com.uws.sys.model.Dic;
import com.uws.sys.service.DicUtil;
import com.uws.sys.service.impl.DicFactory;

/**
* @ClassName: QuesItemController 
* @Description: 选择题目购物车
 */
@Controller
@RequestMapping(Constants.QUESTION_QUESTIONITEM)
public class QuesItemSelectController {
	
	// 日志
	private Logger log = new LoggerFactory(QuesItemSelectController.class);
	//数据字典工具类
	private DicUtil dicUtil = DicFactory.getDicUtil();
	@Autowired
	private IQuesItemService quesItemService;
	@Autowired
	private IQuesInfoService quesInfoService;

	/**
	 * 根据问卷Id，查询对应的多个题目Id
	 * 用于购物车回显选中的题目
	 * @param questionId
	 * @return
	 */
	@RequestMapping(value={"/opt-query/itemIdsQuery"},produces={"text/plain;charset=UTF-8"})
	@ResponseBody
	public String getItemIds(String paperId,String paperType){
		if(DataUtil.isNotNull(paperId) && DataUtil.isNotNull(paperType)){
			StringBuilder itemId = new StringBuilder();
			String itemIds = "";
			//根据问题Id，获取对应的问卷题目List
			List<QuestionInfoItemModel> infoItemList =quesInfoService.getItemByPaperType(paperId,paperType);
			if(infoItemList!=null && infoItemList.size()>0){
				//获取题目的id,拼接成串，用于回显展示
				for(QuestionInfoItemModel infoItem:infoItemList){
					if(infoItem.getQuestionItem()!=null && (infoItem.getQuestionItem().getUseStatus().getId()).equals(Constants.PAPER_STATUS_ENABLE.getId())){
						itemId.append(infoItem.getQuestionItem().getId()+",");
					}
				}
				if(itemId!=null && !itemId.equals("")){
					itemIds = itemId.substring(0, itemId.length()-1);
				}
			}
			return itemIds.toString();
		}
		return "error";
	}
	
	/**
	 * 题目维护—购物车查询列表
	 * @param model
	 * @param request
	 * @param itemName
	 * @param itemTypeId
	 * @param itemCategoryId
	 * @param itemids
	 * @return
	 */
    @RequestMapping({"/comp/queryItemList"})
    public String queryItemList(ModelMap model, HttpServletRequest request, String itemName,
    											String itemTypeId, String itemCategoryId,String itemids,String paperType){
	    log.info("题目维护—购物车查询列表信息");
		int pageNo = request.getParameter("pageNo") != null ? Integer.valueOf(request.getParameter("pageNo")).intValue() : 1;
		Page page = quesItemService.queryItemCompList(5, pageNo, itemName,itemTypeId,itemCategoryId, paperType);
		//获取的值放在Info里
		@SuppressWarnings("unchecked")
		Collection<QuestionItemModel> list = page.getResult();
	    for (QuestionItemModel item : list){
	    	if(item!=null){
	    		item.setInfo("{name:'" + item.getItemName()+ "',Id:'" + item.getId() +"'," +
	    								 "itemCategoryTypeId:'"+item.getItemCategory().getId()+"',itemTypeId:'"+item.getItemType().getId()+"'}");
	    	}
	    }
	    model.addAttribute("itemTypeList",dicUtil.getDicInfoList("ITME_TYPE"));
		model.addAttribute("page", page);
		model.addAttribute("formId", "itemsQuery");
		model.addAttribute("itemids", itemids);
		model.addAttribute("itemName", itemName);
		model.addAttribute("paperType", paperType);
		model.addAttribute("itemTypeId",itemTypeId);
		model.addAttribute("itemCategoryId", itemCategoryId);
        return "/question/itemManage/comp/checkboxItem";
    }
    
    /**
     * 初始化题目数据
     * @param ids
     * @return
     */
    @RequestMapping(value={"/comp/getItemJson"}, produces={"text/plain;charset=UTF-8"})
    @ResponseBody
    public String getItemJson(String ids)
    {
    	if (StringUtils.isEmpty(ids) || ids.equals("undefined")) {
		      return "[]";
		    }
		    StringBuffer sb = new StringBuffer("[");
		    int i = 0;
		    for (String id : ids.split(","))
		    {
		      if (i > 0) {
		        sb.append(",");
		      }
		      QuestionItemModel item =quesItemService.findItemById(id);
		      if(item!=null){
		    	sb.append("{name:'").append(item.getItemName()).append("',")
		    	  .append("Id:'").append(item.getId()).append("',")
		    	  .append("itemCategoryTypeId:'").append(item.getItemCategory().getId()).append("',")
		    	  .append("itemTypeId:'").append(item.getItemType().getId()).append("'")
		    	  .append("}");
		    	  i++;
		      }
		    }
		    return sb.append("]").toString();
	    }
	   public  List<Dic> queryPaperType(String paperType){
		   List<Dic> categoryList = new ArrayList<Dic>();
		    categoryList.add(Constants.QUESINFO_TYPE_COMMON);
		    //若问卷类型为迎新，就显示迎新和公共类型的题目
		    if(Constants.QUESINFO_TYPE_ORIENTATION.getId().equals(paperType)){
		    	categoryList.add(Constants.QUESINFO_TYPE_ORIENTATION);
		    }
		    //若问卷类型为毕业，就显示毕业和公共类型的题目
		    if(Constants.QUESINFO_TYPE_GRADUATION.getId().equals(paperType)){
		    	categoryList.add(Constants.QUESINFO_TYPE_GRADUATION);
		    }
		    //若问卷为公共类型，可以显示所有题目
		    if(Constants.QUESINFO_TYPE_COMMON.getId().equals(paperType)){
		    	categoryList = dicUtil.getDicInfoList("QUESINFO_TYPE");
		    }
		   return categoryList;
	   } 
}
