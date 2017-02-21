(function(){

	$(document).ready(function () {
		//home scroll 
		var doc = $(document),
			win = $(window),
			
			h = $("#head"),
			b = $("#body"),
			f = $("#foot"),
			home = $("#home"),
			shoppingCart = $("#shoppingCart"),
			itemList = $("#itemList"),
			loadDom = $("#loadDom"),
			buyItemList = $("#buyItemList"),
			product = $("#product"),
			loading = false,
			touchStartY = 0,
			oldHeight = loadDom.height(),
			maxHeight = oldHeight*4,
			playAnim = false;
			
			//test 
			for(var i =0;i<6;i+=2){
				itemList.append(loadTpl("#tpl-item",{
					"title1":"title"+i,
					"title2":"title"+(i+1),
					"test":{
						"abc":"abc"
					}
				}));
			}

			for(var i =0;i<10;i++){
				buyItemList.append(loadTpl("#tpl-preItem",{
				 
				}));
			}
			
  		win.unbind("scroll").on('scroll',function(){
			 
			var flag = (doc.height()) <= (window.innerHeight + win.scrollTop());

   			if(!loading && flag) {
				loading =true;
 				setTimeout(function(){
					for(var i =0;i<6;i+=2){
						itemList.append(loadTpl("#tpl-item",{
							"title1":"title"+i,
							"title2":"title"+(i+1),
							"test":{
								"abc":"abc"
							}
						}));
					}
 					
					loading =false;
				},1500);
			}
		});
		home.unbind("touchstart touchmove touchend")
		.on('touchstart',function(e){
            if(loading){
                touchStartY = e.originalEvent.touches[0].pageY|| 0;
            }
        })
		.on('touchmove',function(e){
            if(!loading){
				return;
            }
 			if(playAnim){
				return;
			}
			var currY = e.originalEvent.touches[0].pageY|| 0;
			var height = loadDom.height()+  Math.pow(Math.abs(currY-touchStartY),4);
			height = Math.min(maxHeight,height);
			loadDom.height(height);
        })
		.on('touchend',function(){
 			if(playAnim){
				return;
			}
			playAnim = true;
			loadDom.animate({height: oldHeight}, 800,'linear',function(){
				playAnim = false;
			});
         });
		//shoppingCart
		//NAV_HISTORY
		NAV_HISTORY.register("product",function(ctx){
			this.productCtx = ctx | this.productCtx;
 			b.find(">div").hide();
			product.show();
		});
		
		///////////////////common/////////////////////////
		//tab
		$(".tabToolbar").find(">div").each(function(i,e){
			var d = $(e), 
				p = d.parent(),
				tagId = d.attr("tabRef"),
				groudId = "#"+p.attr("id"),
				tClss = p.attr("tabTClass"),
				rClass = p.attr("tabRClass");
			d.click(function(){
				triggerTab(tagId,groudId,tClss,rClass);
			});
		});
		//checkBoxGroup
		$(".checkBoxGroup").click(function(){
			var $this =  $(this),
				flag = $this.prop('checked'),
				sub = $this.attr('refSub')
 			$("."+sub).each(function(){
				$(this).prop("checked",flag);
			});
		});
	});
})();

if (!String.prototype.format) {                                                 
  String.prototype.format = function() {                                        
	var obj = arguments[0];                                                                                       
	return this.replace(/\{\{((\w+\.?))+\}\}/g, function(match, item,c) {
		var arKeys = match.replace("{{","").replace("}}","").split(".");
		var ret = null;
		if(arKeys.length>1){
			ret = getMapSubValue(obj,arKeys,0);
		}else{
			ret = obj[item];
		}
	  return typeof  ret != 'undefined' ? ret: match;
	});
  };
} 

if (!Array.prototype.remove) {                                                 
  Array.prototype.remove = function(index) {    
	if(index>=this.length || index <0){
		return;
	}
	this.splice(index,1);                                                         
  };                                                                          
}
if (!Array.prototype.removeValue) {                                                 
	Array.prototype.removeValue = function(value) {    
		var i = this.indexOf(value);
		this.remove(i);
	};                                                                
} 
if (!Array.prototype.uniquePush) {                                                 
  Array.prototype.uniquePush = function(value) {    
		this.removeValue(value);
		this.push(value);
  };                              
} 


var NAV_HISTORY = {
	pageCb : {},
	pageLink :[],
	max : 8,
	register:function(id,recoverCallBack){
		if(!this.pageCb[id] && recoverCallBack){
			this.pageCb[id] = recoverCallBack;
 		}
	},
	push:function(id,recoverCallBack){
		this.register(id,recoverCallBack);
		this.pageLink.uniquePush(id);
 		if(this.pageLink.length>this.max){
			this.pageLink.shift();
		}
	},
	pop:function(ctx){
		if(this.pageLink >0){
			var id = this.pageLink.pop();
			var rcb = this.pageCb[id];
			if(rcb){
				rcb(ctx);
			}
		}
	},
	jumpPage:function(curr,ctx){
 		var rcb = this.pageCb[curr];
		if(rcb){
			rcb(ctx);
		}
		this.push(curr);
	}
};

var TPL_DATA ={};
function loadTpl(id,obj){
	var tpl = TPL_DATA[id];
	if(!tpl){
		tpl = $(id).html();
		TPL_DATA[id] = tpl;
	}
	if(obj){
		return tpl.format(obj);
	}
	return tpl;
}

function triggerTab(targetId,groupId,tClss,rClass){
 	$(groupId).find(">div").each(function(i,e){
 		var d = $(e),
			itemId = d.attr("tabRef"),
			itemDom = $("#"+itemId);
		d.removeClass(tClss);
		d.removeClass(rClass);
		itemDom.hide();
		if(itemId==targetId){
			d.addClass(tClss);
			itemDom.show();
			//NAV_HISTORY.push(targetId);
 		}else{
			d.addClass(rClass);
		}
	});
}

function getMapSubValue(map,arKey,os){
	map = map[arKey[os]];
	if(typeof map == 'undefined'){
		return null;
	}
	os++;
	if(arKey.length==os){
		return map;
	}
	return getMapSubValue(map,arKey,os++);
}
function registerMVVM(obj,key,id){
	Object.defineProperty(obj, key, {
		get: function(){
			return $(id).html();
		},
		set: function(v){
			$(id).html(v);
		}
	});
}