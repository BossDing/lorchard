var info_ops = {
	init:function(){
		this.inintComponent();
	},
	inintComponent:function(){
		var id = common_ops.g_getQueryString('id');
      
		$.ajax({
			url:WEB_ROOT+'/goods/'+id,
			type:'get',
			dataType:'json'
		}).done(function(res){
			if(res.code == 0){
			$('.layui-tab-content input[name="name"]').val(res.data.name);
			$('.layui-tab-content textarea[name="description"]').val(res.data.description);
			$.each(res.data.previewImages, function(i,v) {
					$("#uploader-list").append('<div id="" class="file-iteme">' +
		                    '<img style="width: 100px;height: 100px;" src="'+ v.previewImagePath +'">' +
		                    '</div>');
				});
			$('.layui-tab-content input[name="parentCategoryName"]').val(res.data.parentCategoryName);
			$('.layui-tab-content input[name="specificationsDescription"]').val(res.data.specificationsDescription);
			$('.layui-tab-content input[name="originalPrice"]').val(res.data.originalPrice);
			$('.layui-tab-content input[name="minPrice"]').val(res.data.minPrice);
			var recommend =  res.data.recommend == 0 ? '否':'是';
			$('.layui-tab-content input[name="recommend"]').val(recommend);
			$('.layui-tab-content input[name="stock"]').val(res.data.stock);
			$('.layui-tab-content input[name="sales"]').val(res.data.sales);
			}
			
		});
	}	
		
};

$(function(){
	info_ops.init();
});