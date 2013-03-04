hasManyThrough template

<div class="container">
  <div class="row">
    <div class="span12">
      <table class="table table-bordered">
        <thead>
          <tr>
            <th>ID</td>
            <th>Identifier Namespace</th>
            <th>Identifier</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>1</td>
            <td>namespace</td>
            <td>value...</td>
          </tr>
        </tbody>
      </table>
      <a class="accordion-toggle pull-right hmtExpandLink" data-toggle="collapse" data-target="#hmtControls" href="#collapseOne">
        Add
      </a>
      <div id="hmtControls" class="collapse in hmtControlsBlock">
        <div class="container">
          <div class="row">
            <div class="span6">
              SearchProp : <input type="text" name="sp1"/>
              <table class="table table-bordered">
                <thead>
                  <tr>
                    <th>ID</td>
                    <th>Identifier Namespace</th>
                    <th>Identifier</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>1</td>
                    <td>namespace</td>
                    <td>value...</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="span6">
              Additional link data here
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
