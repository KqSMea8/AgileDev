/**
 * 搜索框组件.
 * 
 * @namespace Spark.pages
 * @class Spark.pages.SearchBox
 * @author ChenHui
 */
(function() {
	
	// 定义别名，提高性能
	var Q = Spark.widgets.Query;
	var H = Spark.util.History;
	var Alert = Spark.widgets.Alert;
	var msg = Spark.util.message;
	
	/**
	 * 搜索框组件构造器
	 * 
	 * @constructor
	 */
	Spark.pages.SearchBox = function(options){
		this.options = $.extend({}, this.options, options);
		var me = this, defaultText = msg(this.options.defaultTextKey),
		$input = $(this.options.input), 
		$button = $(this.options.button);
		this.renderKeyword(H.getHash());
		
		$input.keydown(function(event){
			var $this = $(this);
			if(event.keyCode=="13"){
				if( $input.val() == ""){
					me._clearSearch();
				}else{
					me._submitSearch();
				}
			}
		}).focus(function(){
			if( $input.val() == defaultText ){
				$input.val("");
			}
			$(this).css("color","#333");
		}).blur(function(){
			if( $input.val() == ""){
				$input.val(defaultText);
			}
			$(this).css("color","#999");
		}).click(function(){
			if( $input.val() == defaultText ){
				$input.val("");
			}
		});
		
		H.bind("q", function() {
			me.renderKeyword(H.getHash());
		}, false);
	}
	
	Spark.pages.SearchBox.prototype = {
			/**
			 * 选项列表
			 * 
			 * @property options
			 * @type Object
			 */
			options : {
				
				/**
				 * 搜索框元素
				 * 
				 * @property input
				 * @type String | HTMLElement
				 * @default #search-box
				 */
				input: "#search_text",
				
				/**
				 * 搜索按钮
				 * 
				 * @property button
				 * @type String | HTMLElement
				 * @default #search-button
				 */
				button: "#search_button",
				
				/**
				 * 查询条件的标识关键字
				 * 
				 * @property conditionKey
				 * @type String
				 * @default: cardInfo
				 */
				conditionKey: "cardInfo",
				
				/**
				 * 搜索框默认信息
				 * 
				 * @property defaultTextKey
				 * @type String
				 */
				defaultTextKey: "query.searchbox.defaultValue"
				
			},
			
			/**
			 * 查询对象.
			 * 
			 * @property query
			 * @type Spark.widgets.Query
			 */
			query: null,
			
			renderKeyword: function SearchBox_renderKeyword(queryState){
				this.query = new Q(queryState);
				condition = this.query.getConditions(this.options.conditionKey);
				var $input = $(this.options.input);
				$input.val( condition ? condition[0].value : msg(this.options.defaultTextKey));
			},
			
			_submitSearch: function SearchBox__submitSearch (){
				var $input = $(this.options.input);
				
				if($input.val()== msg(this.options.defaultTextKey)){
					Spark.widgets.Alert.alert(msg("query.searchbox.alert.inputQueryString"));
				}else if( $input.val() == ""){
					this._clearSearch();
				}else{
					this.query.removeCondition(this.options.conditionKey);
					this.query.addCondition(this.options.conditionKey, "like", $input.val());
					H.go(this.query.serializeConditions());
				}
			},
			
			_clearSearch: function SearchBox__clearSearch(){
				this.query.removeCondition(this.options.conditionKey);
				var queryString = this.query.serializeConditions();
				if (!queryString){
					queryString = "q=";
				}
				H.go(queryString);
			}
	}
	
})();