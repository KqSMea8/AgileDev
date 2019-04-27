<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>

<spark:template page="../templates/systemmanage.jsp" menu="shadow">
<spark:acl hasPermission="${ADMIN}" domainObject="${sparkSystemResource}" user="${original}">
	<div class="title-bar">
		<h3><fmt:message key="admin.shadow"></fmt:message></h3>
	</div>
	<div id="search-user">
		<fmt:message key="admin.shadow.original"/>${original.name}(${original.username}) &nbsp;
		<c:if test="${shadow == null}">
			<fmt:message key="admin.shadow.shadow.null"/>
		</c:if>
		<c:if test="${shadow != null}">
			<fmt:message key="admin.shadow.shadow"/>${shadow.name}(${shadow.username}) 
		</c:if>
		 <div>
		  	<input id="username" name="username"/>
           	<button type="botton" id="shadow"><fmt:message key="admin.shadow.play"/></button>
		 </div>
		 <div>
		 	<button type="button" id="revert"><fmt:message key="admin.shadow.revert"/></button>
		 </div>
		 <div id="error-message" class="info-message" style="display:none">
			<span class="info-message-icon"></span><span id="info-message"></span>
		</div>
	</div>
	<script type="text/javascript">
		(function(){
			var shadow = function(){
				$.ajax({
					url : "<%= request.getContextPath()%>/system/shadow?username=" + $("#username").val(),
					type : "PUT",
					success : function(data){
						location.reload(true);
					},	
					error : function(x, s, e){
						if(x.status == 403){ 
							$("#info-message").text(Spark.util.message("admin.shadow.forbidden"));
							$("#error-message").show();
						}else if(x.status == 400){
							$("#info-message").text(Spark.util.message("admin.shadow.badRequest"));
							$("#error-message").show();
						}else{
							$("#info-message").text(Spark.util.message("response.status.500"));
							$("#error-message").show();
						}
					}
					
				})
			}
			$("#shadow").click(function(){
				shadow();
			})
			$("#username").keydown(function(e){
				if(e.keyCode=="13"){
					shadow();
				}
			})
			$("#revert").click(function(){
				$.ajax({
					url : "<%= request.getContextPath()%>/system/shadow",
					type : "DELETE",
					success : function(data){
						location.reload();
					},	
					error : function(x, s, e){
					
					}
				})
			})
		})()
	</script>
</spark:acl>
</spark:template>