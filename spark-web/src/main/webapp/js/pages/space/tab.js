(function(){
	
	/**
	 * 空间固定TAB的序列(t)常量定义
	 */
	Spark.constants.TAB_INDEX = {
		/** 产品需求*/
		PRODUCT_TAB : 'product',
		/** 项目视图*/
		PROJECT_TAB : 'project',
		/** 管理空间*/
		MANAGER_TAB : 'admin' 
	};
	
 
	/**
	 * 页面上方标签及面包屑组件.
	 * 
	 * @namespace Spark.pages
	 * @class Spark.pages.Tab
	 * @author shixiaolei
	 */
	Spark.pages.Tab = function(options){ 
		this.options = $.extend(this.options, options);
		this.load();
		return this;
	};
	
	Spark.pages.Tab.prototype={
			
		options : {
			/**
			 * 所属空间前缀
			 */
			prefixCode : null  
			 
		},
		
		/**
		 * 刷新页面的TAB栏.
		 * 每次刷新页面时，会在space.jsp里被调用。
		 * 
		 */
		load: function(){   
		 	var _tab = Spark.util.TabUtils.getCurrentTab();  
		 	var spacePrefix = Spark.constants.CONTEXT + '/spaces/'+ this.options.prefixCode;
		 	$("#common-tabs li").removeClass("selected");
		 	if(_tab && $("#tab-" + _tab)){
		 		$("#tab-" + _tab).addClass("selected");
		 	}else{
		 		$("#tab-product").addClass("selected");
		 	}
		 	Spark.util.TabUtils.setTabToCookie(this.options.prefixCode);
 
			/** 面包屑*/
			if(Spark.util.History.getHash("t")){
				$("#current-tab-breadcrumb").text($("#common-tabs li.selected >a").text());
			}else{
				$('<a href=' + spacePrefix + '></a>')
					.text($("#common-tabs li.selected >a").text())
					.appendTo($("#current-tab-breadcrumb"));
			}
		}
	};
	
	/**
	 * 页面TAB与面包屑的通用工具类，封装若干静态方法。
	 * 
	 * 整体设计如下：
	 * 空间上的TAB，每一个都对应一个唯一的t作为其标识。
	 * 有两种来源：
	 * 一种是空间固定的TAB，目前支持产品需求、项目视图和管理空间三个TAB，分别为这三个TAB赋-1、-2、-3作为其t；
	 * 另一种是自定义收藏的TAB，这些TAB将以其持久化ID做t.
	 * 当点击这些TAB时，对应的t将反映到URL的HASH上，同时将其作为"上次访问TAB"保存在cookie中对应空间的path下。
	 * 
	 * 这样，这些t就成了空间导航的标识，用户点击这些TAB，则这些t反映了当前TAB，它将使标签栏选中/反选；同时反映在面包屑上；
	 * 如果由TAB进入子页面(卡片详情等)，则cookie里的"上次访问TAB"亦可作为标签栏选中/反选和面包屑的操作标准；
	 */
	Spark.util.TabUtils = {
		
		/**
		 * 得到当前页面的TAB。
		 * @method getCurrentTab
		 * 规则是：
		 * 如果在URL的HASH上有t字段，那么说明当前页面是自定义空间收藏或者空间固定TAB，返回这个TAB
		 * 否则，则返回cookie里的"上次访问TAB"
		 */
		getCurrentTab : function(){ 
			var t = Spark.util.History.getHash("t"); 
			if(t){  
				return t;
			}
			t = Spark.util.Cookie.get("last-visited-tab");
			if(t){ 
				return t;
			} 
			return Spark.constants.TAB_INDEX.PRODUCT_TAB;
		},
		
		/**
		 * 将当前TAB加到cookie中对应空间的path下
		 * @method setTabToCookie
		 * @param prefixCode {String} 当前空间前缀
		 * 
		 */
		setTabToCookie : function(prefixCode){
			Spark.util.Cookie.setToPath("last-visited-tab",  Spark.util.TabUtils.getCurrentTab(), Spark.constants.CONTEXT + "/spaces/" + prefixCode  + "/",1);
		},
		
		/**
		 * 将当前TAB加到url的hash上
		 * @method setTabToHash
		 * @param index {Number} 加到t上的值
		 * 
		 */
		setTabToHash : function(index){
			if(!Spark.util.History.getHash("t")){
				Spark.util.History.go("t",index);
			}
		},
		
		/**
		 * 完成设置Hash、设置cookie两步。一般情况下调用这个即可。
		 * @method setTab
		 * @param prefixCode {String} 当前空间前缀
		 * @param index {Number} 加到t上的值
		 * 
		 */
		setTab : function(prefixCode, index){
			Spark.util.TabUtils.setTabToHash(index);
			Spark.util.TabUtils.setTabToCookie(prefixCode);
		},
		
		/**
		 * 得到上次访问TAB
		 */
		getLastVisitTab : function(prefixCode){
			var t = Spark.util.Cookie.get("last-visited-tab");
			var spacePrefix = Spark.constants.CONTEXT + '/spaces/'+ prefixCode;
			if( !t ){
				return spacePrefix + '/cards/list';
			}else if(t == Spark.constants.TAB_INDEX.PRODUCT_TAB){
				return spacePrefix + '/cards/list';
			}else if(t == Spark.constants.TAB_INDEX.PROJECT_TAB){
				return spacePrefix + '/cards/roadmap';
			}else if(t == Spark.constants.TAB_INDEX.MANAGER_TAB){
				return spacePrefix + '/edit';
			}else{
				return spacePrefix + '/spaceviews/' + t;
			}
		},
		
		/**
		 * 将上次访问的页面放入cookie
		 */
		setLastView: function(prefixCode, view){
			Spark.util.Cookie.setToPath("last-visited-view",  view, Spark.constants.CONTEXT + "/spaces/" + prefixCode  + "/",1);
		},
		
		getLastView: function(){
			if(Spark.util.Cookie.get('last-visited-view')){
				return Spark.util.Cookie.get('last-visited-view');
			}else{
				return "/list#";
			}
		}
		
	};
	
})();