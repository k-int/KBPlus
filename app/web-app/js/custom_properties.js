/**
 * Created by ioannis on 27/06/2014.
 */
function runCustomPropsJS(ajaxurl){
    refdatacatsearch(ajaxurl);
    searchProp(ajaxurl);
    showModalOnSelect();
    showHideRefData();
    hideModalOnSubmit();
    //Needs to run to make the xEditable visible
    $('.xEditableValue').editable();
    $('.xEditableManyToOne').editable();
}

function refdatacatsearch (ajaxurl){
    $("#cust_prop_refdatacatsearch").select2({
        placeholder: "Type category...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
            url: ajaxurl,
            dataType: 'json',
            data: function (term, page) {
                return {
                    q: term, // search term
                    page_limit: 10,
                    baseClass:'com.k_int.kbplus.RefdataCategory'
                };
            },
            results: function (data, page) {
                return {results: data.values};
            }
        }
    });
}

function searchProp(ajaxurl){

    $("#customPropSelect").select2({
        placeholder: "Search for a custom property...",
        minimumInputLength: 1,
        ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
            url: ajaxurl,
            dataType: 'json',
            data: function (term, page) {
                return {
                    q: term, // search term
                    page_limit: 10,
                    baseClass:'com.k_int.custprops.PropertyDefinition'
                };
            },
            results: function (data, page) {
                return {results: data.values};
            }
        },
        createSearchChoice:function(term, data) {
            return {id:-1, text:"New Property: "+term};
        }
    });
    }
function showModalOnSelect(){
    $("#customPropSelect").on("select2-selecting", function(e) {
        if(e.val == -1){
            var selectedText = e.object.text;
            selectedText = selectedText.replace("New Property: ","")
            $("input[name='cust_prop_name']" ).val(selectedText);
            $('#cust_prop_add_modal').modal('show');

        }
    });
    }

function showHideRefData() {
    $('#cust_prop_modal_select').change(function() {
        var selectedText = $( "#cust_prop_modal_select option:selected" ).val();
        if( selectedText == "class com.k_int.kbplus.RefdataValue") {
            $("#cust_prop_ref_data_name").show();
        }else{
            $("#cust_prop_ref_data_name").hide();
        }
    });
    }

function hideModalOnSubmit(){
    $("#new_cust_prop_add_btn").click(function(){
        $('#cust_prop_add_modal').modal('hide');
    });
    }
