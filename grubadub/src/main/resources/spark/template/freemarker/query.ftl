<#assign content>
  <div class="container-fluid">
    <div class="row">
      <div class="col-sm-4">
        <div class="row">
          <div class="col-sm-12">
            <button id="location" class="btn btn-default">Current Location</button>
            <input id="destination"class="form-control" placeholder="Destination">
          </div>
        </div>
      </div>
      <div id="map-canvas" class="col-sm-8" style="height:865px;"></div>
    </div>
  </div>
</#assign>
<#include "main.ftl">
