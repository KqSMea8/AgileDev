<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spark" uri="http://spark.baidu.com/tags"%>
<spark:template page="../templates/systemmanage.jsp" menu="user">
	<div class="title-bar">
		<h3><fmt:message key="user.list"></fmt:message></h3>
		<div class="left">
			<a href="new"><fmt:message key="button.user.create"></fmt:message></a>
		</div>
	</div>
	<div id="search-user">
		 <form>
              <input id="keyword" name="keyword" value="${keyword}"/>
              <button type="submit"><fmt:message key="button.search"/></button>
              <table class="list-table align-center">
				<thead>
					<tr>
						<th><fmt:message key="user.userName" /></th>
						<th><fmt:message key="user.name" /></th>
						<th><fmt:message key="user.email" /></th>
						<th><fmt:message key="user.locked"/></th>
						<th><fmt:message key="label.operation"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${users}" var="user">
						<tr>
							<td>${user.username}</td>
							<td>${user.name}</td>
							<td>${user.email}</td>
							<td>${user.locked}</td>
							<td><a href="${user.id}/edit"><fmt:message key="button.edit" /></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
         </form>    
	</div>
</spark:template>