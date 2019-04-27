/**
 * 层级视图组件.
 * 
 * @namespace Spark.pages
 * @class Spark.pages.Project
 * @author shixiaolei
 */
(function() {
	
	// 定义别名，提高性能
	var $window = $(window);

	/**
	 * 层级视图组件构造器.
	 * 
	 * @constructor
	 */
	Spark.pages.Project = function(options) {
		if (!options || !options.prefixCode) {
			throw new Error("Argument of Project constructor must not be null.");
		}
		this.options = $.extend({}, this.options, options);
		this.showProjects();
	}
	
	Spark.pages.Project.prototype = {
		
		/**
		 * 选项列表.
		 * 
		 * @property options
		 * @type Object
		 */
		options: {
			
			/**
			 * 空间别名.
			 * 
			 * @property prefixCode
			 * @type String
			 */
			prefixCode: null,
			
			/**
			 * 层级视图根元素.
			 * 
			 * @property hierarchy
			 * @type String | HTMLElement
			 * @default "#hierarchy"
			 */
			container: "#projects" 
		},
		
		/**
		 * 当前页面显示的所有卡片列表.
		 * <p>
		 * 列表的每个元素包括卡片和DOM元素两部分：
		 * { project: [卡片数据], el: [DOM元素] }
		 * </p>
		 * 
		 * @property cards
		 * @type Array
		 */
		projects: null,
		
		
		/**
		 * 渲染根卡片列表.
		 * 
		 * @method showRoot
		 */
		showProjects: function Project_showProjects() {
			Spark.util.TabUtils.setLastView(this.options.prefixCode ,"/roadmap#" + Spark.util.History.getHash());
			var url = Spark.constants.CONTEXT + "/ajax/spaces/" + this.options.prefixCode + "/projects/list";
			var me = this;
			$.getJSON(url, function(projects) {
				me.projects = projects; 
				me._renderProjects(me.projects, $(me.options.container));
			});
		},
		
		/**
		 * 渲染到页面.
		 * 
		 * @method _render
		 * @param cards {Array} 卡片列表
		 * @param container {HTMLElement} HTML容器元素
		 * @private
		 */
		_renderProjects: function Project__renderProject(projects, container) {
			var project, project_id, i, item, html , me = this;
			
			for (i = 0; i < projects.length; i++) {
					html = [];
					project = projects[i];
					item = $('<div class="project" id="project-' + project.id + '">').appendTo(container);
					html.push('<div class="project-head">');
					html.push('<div class="project-name"><a href="' + Spark.constants.CONTEXT + '/spaces/' + me.options.prefixCode + '/cards/list#q=[project][equals][' + project.id + ']&t=' + Spark.constants.TAB_INDEX.PROJECT_TAB + '">'  + project.name + '</a></div>');
					html.push('<div class="count"><button class="simple button-expand"></button></div>');
					html.push('</div>');
					item.html(html.join("")); 
					if(project.cards.length > 0){
							$('#project-' + project.id + ' .button-expand')
								.bind("click", {project: me.projects[i]} , function(event) {
									var $this = $(this);
									if ($this.hasClass("button-expand")) {
										me.expand(event.data.project);
										$this.removeClass("button-expand").addClass("button-collapse");
									} else {
										me.collapse(event.data.project);
										$this.removeClass("button-collapse").addClass("button-expand");
									}
								});
						 
					}
					$('<span>' + Spark.util.message("project.card.count", project.cards.length) + '</span>').appendTo(item.find(".count"));
			}
		},
		
		expand: function(project){ 
			if(!project.cardLoaded){
				this._renderCards(project);
				project.cardLoaded = true;
			}
			$("#cards-in-project-" + project.id).show();
		},
		
		collapse: function(project){
			$("#cards-in-project-" + project.id).hide();
		},
		
		
		_renderCards: function Project__renderCards(project) {
			var me = this;
			var url = Spark.constants.CONTEXT + "/ajax/spaces/" + me.options.prefixCode + "/projects/" + project.id + "/cards";
			var container = $('<div class="project-body" id="cards-in-project-' + project.id + '">').appendTo($("#project-" + project.id));
			var tableContainer = $('<table class="project-cards-table"></table>').appendTo(container);
			$.getJSON(url, function(cards) {
				var html, card, i, item , trclass;
				for (i = 0; i < cards.length; i++) {
						html = [];
						card = cards[i];
						trclass = ( i%2 == 1 ? 'even' :'odd') ;
						item = $('<tr id="card-' + card.id + '" class="' + trclass + '">').appendTo(tableContainer);
						html.push('<td width="80px"><a href="' + Spark.constants.CONTEXT + '/spaces/' + me.options.prefixCode + '/cards/' + card.sequence + '">' + card.space.prefixCode + '-' + card.sequence + '</a></td>');
						html.push('<td>' + card.title + '</td>');
						item.html(html.join("")); 
					 
				}
			});
			
		}
	}
	
})();