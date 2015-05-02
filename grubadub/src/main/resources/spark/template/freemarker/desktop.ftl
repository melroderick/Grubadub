<#assign content>

<div class="container-fluid full-height desktop">
	<div class="row full-height">
		<div class="col-xs-4 full-height gui">
			<div class="row header-row">
				<header>
					<div class="group">
						<a href="#" id="back-btn" class="btn btn-default"><i class="fa fa-chevron-left fa-lg"></i></a>
						<h1>
							<a href="#"><i class="fa fa-cutlery"></i> Grubadub</a>
						</h1>
					</div>
				</header>
			</div>

			<div class="group" id="main-wrapper"></div>
		</div>
		<div id="map-canvas" class="col-xs-8 full-height"></div>
	</div>
</div>

<script type="text/javascript">var desktop = true;</script>

</#assign>
<#include "main.ftl">