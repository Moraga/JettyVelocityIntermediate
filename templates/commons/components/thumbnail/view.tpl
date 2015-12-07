
<div class="row">
	{{#each data.items}}
	<div class="col-sm-4">
		<div class="thumbnail">
			<img src="http://dummyimage.com/157x4:3">
			<div class="caption">
				<h3>{{ title }}</h3>
				<p>{{ description }}</p>
			</div>
		</div>
	</div>
	{{/each}}
</div>