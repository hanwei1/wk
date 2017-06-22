$(function(){
		$(".level1 .li1 .a1").bind('click',function(){
			var $this = $(this).parent(),
			    $ul = $this.find("ul"),
			    liNum = $ul.children().length,
			    height = $this.height();
			if($ul.css('display') == 'none'){
				var cur = $(".cur1").parent(),
					curlen = cur.find("li").length;
				cur.find("ul").hide(100,"linear");
				cur.animate({height:cur.height()-curlen*30 + "px"},100);
				$(".cur1").removeClass("cur1");
				$(this).addClass("cur1");
				height += liNum * 30;
				$ul.show(100,"linear");
				$this.animate({height:height+"px"},100)	
				
			}else{
				$(this).removeClass("cur1");
				height -= liNum * 30;
				$ul.hide(500,"linear");
				height = height>32?height:32;
				$this.animate({height:height+"px"},100)	
			}
		});
		var url = window.location.href;
		url = url.replace("#","");
		$(".level1").find("li").each(function(){
			if(url.indexOf($(this).find("a").attr("href")) > 0){
				$(this).css("background-color","#9bd1fb").parent().show();
			}
		});
		var height = document.body.clientHeight;
		$(".mnue").height(height-114);
		$(".datalist .list").height(height - $(".content .search").height() - $(".datalist .tt").height() - 191);
	});

function chose(num){
	$("#currentNum").val(num);
	myflush();
}
function jump(tp){
	var num = parseInt($("#currentNum").val());
	if(tp == 1){
		$("#currentNum").val(1);
	}else if(tp == 2){
		if(num > 1)
		$("#currentNum").val(num - 1);
	}else if(tp == 3){
		if(num < parseInt($("#pageNum").val()))
		$("#currentNum").val(num + 1);
	}else{
		$("#currentNum").val($("#pageNum").val());	
	}
	myflush();
}
function createPage(page){
	var currentNum = page.currentPage,
	pagenum = page.pagenum;
	var pagenums = "";
	pagenums += "<input type='hidden' id='currentNum' value='"+currentNum+"'>";
	pagenums += "<input type='hidden' id='pageNum' value='"+pagenum+"'>";
	pagenums += "<div class='pages'><span onclick='jump(1)' class='btn first'></span><span onclick='jump(2)' class='btn prep'></span>";
	$.each(page.pages,function(i,it){
		if(it == currentNum){
			pagenums += "<span class='curr'>"+it+"</span>";
		}else{				
			pagenums += "<span onclick='chose("+it+")'>"+it+"</span>";
		}
	});
	pagenums += "<span onclick='jump(3)' class='btn next'></span><span onclick='jump(4)' class='btn last'></span><i class='line'></i><span onclick='myflush()' class='btn flush'></span></div>";
	$(".page").empty();
	$(".page").append(pagenums);		
}
function initPage(){
	var pagenums = "";
	pagenums += "<input type='hidden' id='currentNum' value='1'>";
	pagenums += "<input type='hidden' id='pageNum' value='10'>";
	$(".page").empty();
	$(".page").append(pagenums);
}
function formatDate(d){
	if(d == undefined){
		return "--";
	}
	var dt = new Date(d);
	var y = dt.getFullYear(),
		m = dt.getMonth() + 1,
		d = dt.getDate(),
		h = dt.getHours(),
		mi = dt.getMinutes(),
		s = dt.getSeconds()
	if(m<10)
		m = "0" + m;
	if(d <10)
		d = "0" + d;
	if(mi <10)
		mi = "0" + mi;
	if(s <10)
		s = "0" + s;
	if(y>1900){
		return y+"-"+m+"-"+d;
	}else{
		return "--";
	}	
}