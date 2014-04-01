$(function () {
  
  $(document).ready(function(){
    
    // Convert each tree select into a nice tree widget.
    $("select.tree").each(function() {
      
      // The tree.      
      var tree = $(this);
      
      // Hidden element id prefix.
      var hidden_prefix = "tree-hidden-";
      
      // Method to ensure there is an id set.
      var ensureID = function (op, count) {
        var currentID = op.attr("id");
        if (!currentID) {
          
         op.attr("id", sName + "-" + count);
        }
      };
      
      var addHiddenField = function (attr, after) {
        
        // Only insert if not already present.
        if ($('hidden#' + attr['id']).length === 0) {
          var field = $("<input type='hidden' />").attr(attr);
          
          // Append after the div.
          $(after).after(field);
        }
      }
      
      // Method to add all options.
      var addOptions = function (ops, parent, level, count) {
        
        // The values.
        var vals = [];
        
        // Number added.
        var added = 0;
        var drop = false;
        
        var parent_id;
        if (typeof parent === 'undefined') {
          parent_id = '#';
        } else {
          parent_id = parent.attr("id");
        }
        
        // Default to 1.
        if (typeof level === 'undefined') level = 1;
        
        // Default to 0.
        if (typeof count === 'undefined') count = 0;
        
        // Each element.
        for (; !drop && count<ops.length; count++) {
          var op = $(ops.get(count));
          
          // Ensure there is an id for the element.
          ensureID(op, count);
          
          var add = false;
          $.each(op.attr("class").split(' '), function() {
            
            var val = this.toString();
            
            // Check for a level class.
            switch (val) {
              case "level" + (level) :
                // This level.
                
                // The attributes.
                var attrs = {
                  "parent"      : parent_id,
                  "id"          : op.attr("id"),
                  "text"        : op.text(),
                  "li_attr"     : {
                    "data-value" : op.attr("value")
                  }
                };
                
                // Push the node.
                vals.push(attrs);
                break;
              case "level" + (level - 1) :
                // Previous level. Don't add, just drop out.
                drop = true;
                break;
                
              case "level" + (level + 1) :
                // Next level.
                var children = addOptions (ops, $(ops.get((count - 1))), level + 1, count)
                vals = vals.concat (children);
                count = count + (children.length - 1);
                break;
            }
          });
        }
        
        return vals;
      };
  
      // Grab the necessary values.
      var sName = tree.attr("name");
      var sMultiple = tree.prop("multiple");
      
      // Grab the options.
      var options = $('option', tree);
  
      // Div to hold the tree.
      var tree_div = $("<div class='tree' />").attr("id", sName + "-tree").jstree({
        'core' : {
          'data'      : addOptions (options),
          'multiple'  : sMultiple
        },
        "checkbox" : {
          "keep_selected_style"  : false,
        },
        "types" : {
          "default" : {
            "valid_children"  : ["default"],
            "icon"            : "icon-code"
          },
        },
        "plugins" : [ "checkbox", "types", "state"]
      });
      
      // Add event listeners.
      tree_div.on("select_node.jstree", function (e, data) {
        
        // Get the jstree instance.
        var jstree = $(this).jstree(true);
        
        // Build the list of ids that we need to remove.
        var ids = [];
        
        var actOnChildren = function (children) {
          $.each (children, function (index, child_id) {
              
            // Add the id of each child and check it's children.
            var child = jstree.get_node(child_id);
            
            if (child.children.length > 0) {
              
              // Act on the children of this element too.
              actOnChildren (child.children);
              
            } else {
              
              // This is a leaf. Let's add a hidden field.
              addHiddenField ({
                "name"  : sName,
                "value" : child.li_attr['data-value'],
                "id"    : hidden_prefix + child_id
              }, tree_div);
            }
          });
        };
        
        if (data.node.children.length > 0) {
          
          // Act on the children of this element too.
          actOnChildren (data.node.children);
          
        } else {
          
          // Add a hidden element.
          addHiddenField ({
            "name"  : sName,
            "value" : data.node['li_attr']['data-value'],
            "id"    : hidden_prefix + data.node['id']
          }, tree_div);
        }
        
      }).on("deselect_node.jstree", function (e, data) {
        
        // Get the jstreee instance.
        var jstree = $(this).jstree(true);
        
        // Method to act on the children of this element.
        var actOnChildren = function (children) {
          $.each (children, function (index, child_id) {
            
            var child = jstree.get_node(child_id);
            
            // Add the hidden field.
            ids.push ("#" + hidden_prefix + child_id);
            
            // Act on the children of this element too.
            actOnChildren (child.children);
          });
        };
        
        // Build the list of ids that we need to remove.
        var ids = ["#" + hidden_prefix + data.node.id];
        
        // Add the parent ids.
        $.each (data.node.parents, function (i, p) {
          if (p != '#') ids.push("#" + hidden_prefix + p);
        });
        
        // Act on all children too.
        actOnChildren(data.node.children)
        
        // Remove all the linked hidden elements.
        $("" + ids).remove();
      });
      
      // Replace the original list with the div, followed by the hidden element.
      tree.replaceWith(tree_div);
    });
  });
});