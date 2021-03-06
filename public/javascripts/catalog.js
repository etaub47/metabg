
$(function() {

    // select game
    $('#game').change(function() {
        $('#createTableDiv').css('display', 'none');
        $('#seatDiv').css('display', 'none');
        if ($(this).val() == 'Select')
            $('#tableDiv').css('display', 'none');
        else {
            jsRoutes.controllers.Application.tables($(this).val()).ajax({
                success: function(data) {
                    $('#table').empty();
                    $('#table').append($('<option></option>').attr('value', 'Select').text('None Selected'));
                    for (var s = data.minPlayers; s <= data.maxPlayers; s++)
                        $('#numPlayers').append($('<option></option>').attr('value', s).text(s));
                    for (var m in data.modes)
                        $('#mode').append($('<option></option>').attr('value', data.modes[m]).text(data.modes[m]));
                    tables = data.tables;
                    for (var t in tables) {
                        table = tables[t];
                        numSeats = table.seats.length
                        availableSeats = 0;
                        for (var s in table.seats)
                            if (table.seats[s] == 'Empty') availableSeats++;
                        tableDesc = table.name + " - " + table.created + " - " + availableSeats + "/" + numSeats;                 
                        $('#table').append($('<option></option>').attr('value', table.name).text(tableDesc));
                    }
                    $('#table').append($('<option></option>').attr('value', 'Create').text('Create New Table'));
                    $('#tableDiv').css('display', 'block');
                }
            });
        }
    });

    // select table
    $('#table').change(function() {
        if ($(this).val() == 'Create') {
            $('#createTableDiv').css('display', 'block');
            $('#seatDiv').css('display', 'none');
        } 
        else {
            $('#createTableDiv').css('display', 'none');
            $('#seatDiv').css('display', 'none');
            if ($(this).val() != 'Select') {
                jsRoutes.controllers.Application.seats($('#game').val(), $(this).val()).ajax({
                    success: function(data) {
                        $('#seat').empty();
                        $('#seat').append($('<option></option>').attr('value', 'Select').text('None Selected'));            
                        for (var s in data)
                            $('#seat').append($('<option></option>').attr('value', s).text(
                                    (parseInt(s) + 1).toString() + ' - ' + data[s]));
                        $('#seatDiv').css('display', 'block');
                    }
                });
            }
        }
    });

    // create table
    $('#create').click(function() {
        jsRoutes.controllers.Application.createTable($('#game').val(), $('#createTable').val(), 
            $('#numPlayers').val(), $('#mode').val()).ajax({
            success: function(data) {
                $('#createTableDiv').css('display', 'none');
                jsRoutes.controllers.Application.tables($('#game').val()).ajax({
                    success: function(data) {
                        $('#table').empty();
                        $('#table').append($('<option></option>').attr('value', 'Select').text('None Selected'));
                        tables = data.tables;
                        for (var t in tables) {
                            table = tables[t];
                            numSeats = table.seats.length
                            availableSeats = 0;
                            for (var s in table.seats)
                                if (table.seats[s] == 'Empty') availableSeats++;
                            tableDesc = table.name + " - " + table.created + " - " + availableSeats + "/" + numSeats;                 
                            $('#table').append($('<option></option>').attr('value', table.name).text(tableDesc));
                        }
                        $('#table').append($('<option></option>').attr('value', 'Create').text('Create New Table'));
                        $('#tableDiv').css('display', 'block');
                    }
                });
            }
        });                
    });
    
    // play game
    $('#sit').click(function() {
        url = "/metabg/" + $('#game').val() + "/" + $('#table').val() + "/" + $('#seat').val();
        if ($('#playerName').val() && $('#playerName').val() != '')
            url += "?player=" + $('#playerName').val();
        document.location.href = url;
    });

})
