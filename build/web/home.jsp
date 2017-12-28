<%-- 
    Document   : home
    Created on : Dec 22, 2017, 3:39:10 PM
    Author     : soumukil
--%>

<%@page import="java.util.List"%>
<%@page import="proverbs.models.ProverbViewModel"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script src="//code.jquery.com/jquery-2.1.0.min.js" type="text/javascript"></script>
        <script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
        <style>
            div.container {
                width: 100%;
                border: 1px solid gray;
            }

            header, footer {
                padding: 1em;
                color: white;
                background-color: black;
                clear: left;
                text-align: center;
            }

            nav {
                float: left;
                max-width: 160px;
                margin: 0;
                padding: 2em;
            }

            nav ul {
                list-style-type: none;
                padding: 0;
            }

            nav ul a {
                text-decoration: none;
            }

            article {
                margin-left: 170px;
                border-left: 1px solid gray;
                padding: 1em;
                overflow: hidden;
            }

            .container1 {
                width: 500px;
                padding: 4px;
                margin: 4px;
            }
            .row {
                text-align: left;
            }
            .btn {
                display: inline-block;
                margin-right: 6px;
                background-color: #EEE;
            }
        </style>
    </head>
    <body>
        <script type="text/javascript">
            function getNext() {
                var data = $("#next").attr("data");
                var prevData = $("#prev").attr("data");
                var xhr = new XMLHttpRequest();
                var urlPath = encodeURI("/ProverbWebApp/getProverb?start=" + data);
                xhr.open("GET", urlPath, true);
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        $("#next").attr("data", parseInt(data) + 8);
                        $("#prev").attr("data", parseInt(prevData) + 8);
                        $("#article").html(xhr.responseText);
                    } else if (xhr.readyState == 4 && xhr.status == 500) {
                        alert("Failed to fetch metadata!!!");
                    }
                }
                xhr.send();
            }

            function getPrev() {
                var data = $("#prev").attr("data");
                var nextData = $("#next").attr("data");
                var xhr = new XMLHttpRequest();
                var urlPath = encodeURI("/ProverbWebApp/getProverb?start=" + data);
                xhr.open("GET", urlPath, true);
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.onreadystatechange = function () {
                    if (xhr.readyState == 4 && xhr.status == 200) {
                        if (parseInt(data) != 0) {
                            $("#prev").attr("data", parseInt(data) - 8);
                            $("#next").attr("data", parseInt(nextData) - 8);
                        }
                        $("#article").html(xhr.responseText);
                    } else if (xhr.readyState == 4 && xhr.status == 500) {
                        alert("Failed to fetch metadata!!!");
                    }
                }
                xhr.send();
            }
        </script>

        <div class="container">

            <header>
                <h1>Proverbs' List</h1>
            </header>
            <nav>
                <table aligb="center">
                    <tr>
                        <td><input type="button" value="Prev" onclick="getPrev()" id="prev" data="0"></td>
                        <td><input type="button" value="Next" onclick="getNext()" id="next" data="8"></td>
                    </tr>
                </table>
            </nav>

            <div id="article">
                <%
                    ArrayList<ProverbViewModel> list = (ArrayList<ProverbViewModel>) request.getAttribute("viewList");
                    for (ProverbViewModel viewModel : list) {
                %>
                <article>
                    <h1><%= viewModel.getText()%></h1>
                    <div class='container1'>
                        <div class='row'>
                            <%
                                List<String> tagList = viewModel.getTags();
                                for (String tag : tagList) {
                            %>
                            <div class='btn'><%= tag%></div>
                            <%
                                }
                            %>
                        </div>
                    </div>
                    <hr/>
                </article>

                <%
                    }
                %>
            </div>
            <footer>Copyright &copy; amazon.com</footer>
        </div>
    </body>
</html>
