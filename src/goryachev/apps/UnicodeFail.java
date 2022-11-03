/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package goryachev.apps;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Unicode / TextArea tester.
 */
public class UnicodeFail extends Application {
    public enum Demo {
        ARABIC, // JDK-8296342 TextArea/Text: Fails to render Arabic
        BROKEN_NAV, // JDK-8296266 Navigation breaks for complex unicode strings
    }

    public static void main(String[] args) {
        Application.launch(UnicodeFail.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ComboBox<Demo> demoField = new ComboBox<Demo>();
        demoField.getItems().addAll(Demo.values());

        TextArea textField = new TextArea();
        
        BorderPane bp = new BorderPane();
        bp.setTop(new HBox(
            demoField
        ));
        bp.setCenter(textField);

        stage.setScene(new Scene(bp));
        stage.setTitle("Unicode Failure JDK-? " + System.getProperty("java.version"));
        stage.setWidth(800);
        stage.setHeight(500);
        stage.show();
        
        demoField.getSelectionModel().selectedItemProperty().addListener((s,prev,c) -> {
            String text = getText(c);
            textField.setText(text);
            textField.positionCaret(0);
            
            Object x = getDescription(c);
            Node n;
            if (x instanceof Node) {
                n = (Node)x;
            } else if (x == null) {
                n = null;
            } else {
                n = new Text(x.toString());
            }
            BorderPane p = new BorderPane();
            p.setLeft(n);
            p.setPadding(new Insets(10, 10, 10, 10));
            bp.setBottom(p);
        });
        
        Platform.runLater(() -> {
            demoField.getSelectionModel().select(Demo.ARABIC);
        });
    }
    
    protected String getText(Demo d) {
        switch(d) {
        case ARABIC:
            return "[ ﷽ ]";
        case BROKEN_NAV:
            return "\n"
                + "Ỏ̷͖͈̞̩͎̻̫̫̜͉̠̫͕̭̭̫̫̹̗̹͈̼̠̖͍͚̥͈̮̼͕̠̤̯̻̥̬̗̼̳̤̳̬̪̹͚̞̼̠͕̼̠̦͚̫͔̯̹͉͉̘͎͕̼̣̝͙̱̟̹̩̟̳̦̭͉̮̖̭̣̣̞̙̗̜̺̭̻̥͚͙̝̦̲̱͉͖͉̰̦͎̫̣̼͎͍̠̮͓̹̹͉̤̰̗̙͕͇͔̱͕̭͈̳̗̭͔̘̖̺̮̜̠͖̘͓̳͕̟̠̱̫̤͓͔̘̰̲͙͍͇̙͎̣̼̗̖͙̯͉̠̟͈͍͕̪͓̝̩̦̖̹̼̠̘̮͚̟͉̺̜͍͓̯̳̱̻͕̣̳͉̻̭̭̱͍̪̩̭̺͕̺̼̥̪͖̦̟͎̻̰_Ỏ̷͖͈̞̩͎̻̫̫̜͉̠̫͕̭̭̫̫̹̗̹͈̼̠̖͍͚̥͈̮̼͕̠̤̯̻̥̬̗̼̳̤̳̬̪̹͚̞̼̠͕̼̠̦͚̫͔̯̹͉͉̘͎͕̼̣̝͙̱̟̹̩̟̳̦̭͉̮̖̭̣̣̞̙̗̜̺̭̻̥͚͙̝̦̲̱͉͖͉̰̦͎̫̣̼͎͍̠̮͓̹̹͉̤̰̗̙͕͇͔̱͕̭͈̳̗̭͔̘̖̺̮̜̠͖̘͓̳͕̟̠̱̫̤͓͔̘̰̲͙͍͇̙͎̣̼̗̖͙̯͉̠̟͈͍͕̪͓̝̩̦̖̹̼̠̘̮͚̟͉̺̜͍͓̯̳̱̻͕̣̳͉̻̭̭̱͍̪̩̭̺͕̺̼̥̪͖̦̟͎̻̰\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "ʅ͡͡͡͡͡͡͡͡͡͡͡ (ƟӨ) ʃ͡͡͡͡͡͡͡͡͡͡ ꐑ (ཀ ඊູ ఠీੂ೧ູ࿃ूੂ✧✧✧✧✧✧ළඕั࿃ूੂ࿃ूੂ) · ⣎⡇ꉺლ༽இ•̛)ྀ◞ ༎ຶ ༽ৣৢ؞ৢ؞ؖ ꉺლ — ʅ͡͡͡͡͡͡͡͡͡͡͡(ƟӨ)ʃ͡͡͡͡͡͡͡͡͡͡ ꐑ(ཀ ඊູ ఠీੂ೧ູ࿃ूੂ✧ළඕั࿃ूੂ࿃ूੂੂ࿃ूੂළඕั✧ı̴̴̡ ̡̡͡|̲̲̲͡ ̲̲̲͡͡π̲̲͡͡ ɵੂ≢࿃ूੂ೧ູఠీੂ ඊູཀ ꐑ(ʅ͡͡͡͡͡͡͡͡͡͡͡(ƟӨ)ʃ͡͡͡͡͡͡͡͡͡͡\n"
                + "\n"
                + "\n";
        default:
            return "?" + d;
        }
    }
    
    protected Object getDescription(Demo d) {
        switch(d) {
        case ARABIC:
            if(false) {
                return new Text("[ ﷽ ]");
            }

            byte[] b = Base64.getDecoder().decode(
                  "iVBORw0KGgoAAAANSUhEUgAAANQAAAA6CAYAAADIkHfqAAAMP2lDQ1BJQ0MgUHJvZmlsZQAA"
                  + "SImVVwdYU8kWnltSIbTQpYTeBBEpAaSE0AJIL4KohCRAKDEGgoq9LCq4FlREwYauiih2mgVF"
                  + "7CyKvS8WVJR1sWBX3qSArvvK9+b75s5//znznzPnzr1zBwD141yxOBfVACBPVCCJDQlgjE1O"
                  + "YZCeADIwARqACjAuL1/Mio6OALAMtn8v764DRNZecZRp/bP/vxZNviCfBwASDXE6P5+XB/FB"
                  + "APAqnlhSAABRxltMKRDLMKxAWwIDhHiRDGcqcJUMpyvwXrlNfCwb4jYAyKpcriQTALVLkGcU"
                  + "8jKhhlofxM4ivlAEgDoDYt+8vEl8iNMgtoU2Yohl+sz0H3Qy/6aZPqTJ5WYOYcVc5IUcKMwX"
                  + "53Kn/Z/p+N8lL1c66MMaVtUsSWisbM4wbzdzJoXLsCrEvaL0yCiItSD+IOTL7SFGqVnS0ASF"
                  + "PWrEy2fDnAFdiJ353MBwiI0gDhblRkYo+fQMYTAHYrhC0KnCAk48xPoQLxLkB8UpbTZJJsUq"
                  + "faH1GRI2S8mf5UrkfmW+7ktzElhK/ddZAo5SH1MryopPgpgKsWWhMDESYjWInfJz4sKVNqOL"
                  + "stiRgzYSaawsfkuIYwWikACFPlaYIQmOVdqX5OUPzhfblCXkRCrx/oKs+FBFfrA2HlceP5wL"
                  + "dkkgYiUM6gjyx0YMzoUvCAxSzB17JhAlxCl1PogLAmIVY3GqODdaaY+bC3JDZLw5xK75hXHK"
                  + "sXhiAVyQCn08Q1wQHa+IEy/K5oZFK+LBl4MIwAaBgAGksKaDSSAbCDt6G3rhnaInGHCBBGQC"
                  + "AXBUMoMjkuQ9IniNA0XgT4gEIH9oXIC8VwAKIf91iFVcHUGGvLdQPiIHPIE4D4SDXHgvlY8S"
                  + "DXlLBI8hI/yHdy6sPBhvLqyy/n/PD7LfGRZkIpSMdNAjQ33QkhhEDCSGEoOJdrgh7ot74xHw"
                  + "6g+rC87EPQfn8d2e8ITQSXhIuEboItyaKJwn+SnKMaAL6gcrc5H+Yy5wa6jphgfgPlAdKuO6"
                  + "uCFwxF2hHxbuBz27QZatjFuWFcZP2n+bwQ9PQ2lHcaagFD2KP8X255Fq9mpuQyqyXP+YH0Ws"
                  + "6UP5Zg/1/Oyf/UP2+bAN/9kSW4QdwM5gJ7Bz2BGsATCwFqwRa8eOyvDQ6nosX12D3mLl8eRA"
                  + "HeE//A0+WVkm851rnXucvyj6CgRTZd9owJ4kniYRZmYVMFhwRxAwOCKe03CGi7OLCwCy/UXx"
                  + "+XoTI983EN3279z8PwDwaRkYGDj8nQtrAWCfB3z9m75ztky4dagAcLaJJ5UUKjhcdiHAr4Q6"
                  + "fNMM4P5lAWzhfFyAO/AG/iAIhIEoEA+SwQQYfRZc5xIwBcwAc0ExKAXLwWqwDmwEW8AOsBvs"
                  + "Bw3gCDgBToML4BK4Bu7A1dMNXoA+8A58RhCEhNAQOmKAmCJWiAPigjARXyQIiUBikWQkDclE"
                  + "RIgUmYHMR0qRMmQdshmpQfYhTcgJ5BzSidxCHiA9yGvkE4qhqqg2aoxaoyNQJspCw9F4dDya"
                  + "iU5Gi9AF6FK0Aq1Gd6H16An0AnoN7UJfoP0YwFQwXcwMc8SYGBuLwlKwDEyCzcJKsHKsGqvD"
                  + "muFzvoJ1Yb3YR5yI03EG7ghXcCiegPPwyfgsfAm+Dt+B1+Nt+BX8Ad6HfyPQCEYEB4IXgUMY"
                  + "S8gkTCEUE8oJ2wiHCKfgu9RNeEckEnWJNkQP+C4mE7OJ04lLiOuJe4jHiZ3ER8R+EolkQHIg"
                  + "+ZCiSFxSAamYtJa0i9RCukzqJn0gq5BNyS7kYHIKWUSeRy4n7yQfI18mPyV/pmhQrChelCgK"
                  + "nzKNsoyyldJMuUjppnymalJtqD7UeGo2dS61glpHPUW9S32joqJiruKpEqMiVJmjUqGyV+Ws"
                  + "ygOVj6paqvaqbNVUVanqUtXtqsdVb6m+odFo1jR/WgqtgLaUVkM7SbtP+6BGV3NS46jx1War"
                  + "VarVq11We6lOUbdSZ6lPUC9SL1c/oH5RvVeDomGtwdbgaszSqNRo0rih0a9J1xypGaWZp7lE"
                  + "c6fmOc1nWiQta60gLb7WAq0tWie1HtExugWdTefR59O30k/Ru7WJ2jbaHO1s7VLt3dod2n06"
                  + "WjquOok6U3UqdY7qdOliuta6HN1c3WW6+3Wv637SM9Zj6Qn0FuvV6V3We68/TN9fX6Bfor9H"
                  + "/5r+JwOGQZBBjsEKgwaDe4a4ob1hjOEUww2Gpwx7h2kP8x7GG1YybP+w20aokb1RrNF0oy1G"
                  + "7Ub9xibGIcZi47XGJ417TXRN/E2yTVaZHDPpMaWb+poKTVeZtpg+Z+gwWIxcRgWjjdFnZmQW"
                  + "aiY122zWYfbZ3MY8wXye+R7zexZUC6ZFhsUqi1aLPktTyzGWMyxrLW9bUayYVllWa6zOWL23"
                  + "trFOsl5o3WD9zEbfhmNTZFNrc9eWZutnO9m22vaqHdGOaZdjt97ukj1q72afZV9pf9EBdXB3"
                  + "EDqsd+gcThjuOVw0vHr4DUdVR5ZjoWOt4wMnXacIp3lODU4vR1iOSBmxYsSZEd+c3Zxznbc6"
                  + "3xmpNTJs5LyRzSNfu9i78FwqXa6Ooo0KHjV7VOOoV64OrgLXDa433ehuY9wWurW6fXX3cJe4"
                  + "17n3eFh6pHlUedxgajOjmUuYZz0JngGesz2PeH70cvcq8Nrv9Ze3o3eO907vZ6NtRgtGbx39"
                  + "yMfch+uz2afLl+Gb5rvJt8vPzI/rV+330N/Cn++/zf8py46VzdrFehngHCAJOBTwnu3Fnsk+"
                  + "HogFhgSWBHYEaQUlBK0Luh9sHpwZXBvcF+IWMj3keCghNDx0RegNjjGHx6nh9IV5hM0MawtX"
                  + "DY8LXxf+MMI+QhLRPAYdEzZm5Zi7kVaRosiGKBDFiVoZdS/aJnpy9OEYYkx0TGXMk9iRsTNi"
                  + "z8TR4ybG7Yx7Fx8Qvyz+ToJtgjShNVE9MTWxJvF9UmBSWVLX2BFjZ469kGyYLExuTCGlJKZs"
                  + "S+kfFzRu9bjuVLfU4tTr423GTx1/boLhhNwJRyeqT+ROPJBGSEtK25n2hRvFreb2p3PSq9L7"
                  + "eGzeGt4Lvj9/Fb9H4CMoEzzN8Mkoy3iW6ZO5MrMnyy+rPKtXyBauE77KDs3emP0+Jypne85A"
                  + "blLunjxyXlpek0hLlCNqm2QyaeqkTrGDuFjcNdlr8urJfZJwybZ8JH98fmOBNvyRb5faSn+R"
                  + "Pij0Laws/DAlccqBqZpTRVPbp9lPWzztaVFw0W/T8em86a0zzGbMnfFgJmvm5lnIrPRZrbMt"
                  + "Zi+Y3T0nZM6OudS5OXN/n+c8r2ze2/lJ85sXGC+Ys+DRLyG/1BarFUuKbyz0XrhxEb5IuKhj"
                  + "8ajFaxd/K+GXnC91Li0v/bKEt+T8ryN/rfh1YGnG0o5l7ss2LCcuFy2/vsJvxY4yzbKiskcr"
                  + "x6ysX8VYVbLq7eqJq8+Vu5ZvXENdI13TVRFR0bjWcu3ytV/WZa27VhlQuafKqGpx1fv1/PWX"
                  + "N/hvqNtovLF046dNwk03N4dsrq+2ri7fQtxSuOXJ1sStZ35j/lazzXBb6bav20Xbu3bE7mir"
                  + "8aip2Wm0c1ktWiut7dmVuuvS7sDdjXWOdZv36O4p3Qv2Svc+35e27/r+8P2tB5gH6g5aHaw6"
                  + "RD9UUo/UT6vva8hq6GpMbuxsCmtqbfZuPnTY6fD2I2ZHKo/qHF12jHpswbGBlqKW/uPi470n"
                  + "Mk88ap3Yeufk2JNX22LaOk6Fnzp7Ovj0yTOsMy1nfc4eOed1ruk883zDBfcL9e1u7Yd+d/v9"
                  + "UId7R/1Fj4uNlzwvNXeO7jx22e/yiSuBV05f5Vy9cC3yWuf1hOs3b6Te6LrJv/nsVu6tV7cL"
                  + "b3++M+cu4W7JPY175feN7lf/YffHni73rqMPAh+0P4x7eOcR79GLx/mPv3QveEJ7Uv7U9GnN"
                  + "M5dnR3qCey49H/e8+4X4xefe4j81/6x6afvy4F/+f7X3je3rfiV5NfB6yRuDN9vfur5t7Y/u"
                  + "v/8u793n9yUfDD7s+Mj8eOZT0qenn6d8IX2p+Gr3tflb+Le7A3kDA2KuhCv/FcBgRTMyAHi9"
                  + "HQBaMgB0eD6jjlOc/+QFUZxZ5Qj8J6w4I8qLOwB18P89phf+3dwAYO9WePyC+uqpAETTAIj3"
                  + "BOioUUN18KwmP1fKChGeAzZFfk3PSwf/pijOnD/E/XMLZKqu4Of2X3GxfElCBSXtAAAAimVY"
                  + "SWZNTQAqAAAACAAEARoABQAAAAEAAAA+ARsABQAAAAEAAABGASgAAwAAAAEAAgAAh2kABAAA"
                  + "AAEAAABOAAAAAAAAAJAAAAABAAAAkAAAAAEAA5KGAAcAAAASAAAAeKACAAQAAAABAAAA1KAD"
                  + "AAQAAAABAAAAOgAAAABBU0NJSQAAAFNjcmVlbnNob3QCb+q2AAAACXBIWXMAABYlAAAWJQFJ"
                  + "UiTwAAAB1WlUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPHg6eG1wbWV0YSB4bWxuczp4PSJh"
                  + "ZG9iZTpuczptZXRhLyIgeDp4bXB0az0iWE1QIENvcmUgNi4wLjAiPgogICA8cmRmOlJERiB4"
                  + "bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMi"
                  + "PgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxu"
                  + "czpleGlmPSJodHRwOi8vbnMuYWRvYmUuY29tL2V4aWYvMS4wLyI+CiAgICAgICAgIDxleGlm"
                  + "OlBpeGVsWURpbWVuc2lvbj41ODwvZXhpZjpQaXhlbFlEaW1lbnNpb24+CiAgICAgICAgIDxl"
                  + "eGlmOlBpeGVsWERpbWVuc2lvbj4yMTI8L2V4aWY6UGl4ZWxYRGltZW5zaW9uPgogICAgICAg"
                  + "ICA8ZXhpZjpVc2VyQ29tbWVudD5TY3JlZW5zaG90PC9leGlmOlVzZXJDb21tZW50PgogICAg"
                  + "ICA8L3JkZjpEZXNjcmlwdGlvbj4KICAgPC9yZGY6UkRGPgo8L3g6eG1wbWV0YT4KojFk3gAA"
                  + "ABxpRE9UAAAAAgAAAAAAAAAdAAAAKAAAAB0AAAAdAAAHRivjCQ0AAAcSSURBVHgB7FtncBVV"
                  + "FL4JD5IoELGN/GHGMiIgvSmKiMNgo4OAWCgjRaSNQlTKCFgogo460tEBRQhFagBpAlI0I4lI"
                  + "DyEoICUSkIF9b/vxfbvse7v7su9NmAUC3DOTye7ee88997vnu+fck0kShYVx4QhwBHxBIIkT"
                  + "yhccuRKOgIEAJxR3BI6AjwhwQvkIJlfFEeCE4j7AEfARAU4oH8HkqjgCnFDcBzgCPiLACeUj"
                  + "mFwVR4ATivsAR8BHBDihfASTq+IIcEJxH+AI+IgAJ5SPYHJVHAFOKO4DHAEfEeCE8hFMrooj"
                  + "wAnFfYAj4CMCnFA+gslVcQQ4obgPcAR8RMCTUIpCjHTvmcqlJHk38haOwA2MgKoS0zXvBcTz"
                  + "fU9CDe4isbWLvbX+ciKN3Vv5xiaVLBHL308seIlYg6ZlHAgeOaizLVkae31wgAUC/q9T14mt"
                  + "ztRYhfQk1uwF59wOQ+K87MvRWUHYztbdAnF68aaSIjCqn8wWzlQ9h63PS2VVHkwutj2pbb0g"
                  + "ZUwox5q0cG6qRajOvQMsJTV27JCxZVn5iv47WuxM/n0pOKSz3B06252tsz+zNXZ4L7Gk8BJm"
                  + "rEqJrF8Ik6voDLEBHSV2aA+x7LNpLL3Sla2zqJDYsrkq6/l2gCUnR3UUniK2cp7K5k9XWbU6"
                  + "yeyrRSklWqQsE8vZrrP3e8mG/ZsK0ko0nneOj0BWpspyd8amZ1vXaOzvfGJZe1LZQ9WdhMro"
                  + "IbG8PeExDycLtHaxgv+Ed8igziKh7dxZ3fH9Wr6cPKbRkYMayXLJbVAUnXZnqzTrU5n6tRWp"
                  + "8T2CsR6sqdHdAvVtI9L08TLtz9UiS9q5SaW6FaP9Jo+QIm3uB03ztqngkEYj+oj0aJpA08bJ"
                  + "jqEj+0oRO56qEqQDu6PzOzqGX86cjJ1jybdKZHzddIGWzondO7ce/u4PAmMGmnt3eF/snr1Y"
                  + "M2jsi0GodT/Gbsr1JtTGFQrVut10bhAgEang4Ht3aTRzoky9ngtRnQpRYrSsGqT3eom0cJZC"
                  + "+Qc00sP5VnEyb4pMB/7QCHOfPhEF7fQ/Gp34K/q+ZpFi6G9wp0AgiCUgx8CXRKpWVqA324n0"
                  + "+zbVaJJEcz6sAXPgkAB5QXovgR0dG4XITdzPRkq0fYNKC6YrdOp41CYvPfy7fwjEI1Tr2nZC"
                  + "LS19hJrykWyc8ogo496JOq0dHkSwH6bKNKCTSA3vMglUI0WgLk+EaEKGROuXKVRU6O200HXp"
                  + "ou4gBZx9w3InHnO+kKnnsyFj6osXdOrfQaSXm4aME2n2JDMCLf5GoZq3CYbNiFB2GTNAMsiO"
                  + "byAIbEsk54t0evw+gdYVsze5O1XPQyGRXt5+5QjEJVQdG6HcDoQpr3eEgqMO7iLS7Mmyw3kQ"
                  + "bj8fJVGbuuYCqpYRqF39IE18V6Jt61QKBeMTyA1nzg6V6t0hRObIylQIwNkF0QnREnNLkk6j"
                  + "+knUuUnISBmtftM+kQmRzC4WmZ+vEaQ+rU1CHjtiRh57P6/nzBkKISK5ZVh3kfL2Oudy9+Hv"
                  + "/iMQj1CoReDwN1K+zavN1MRuwvUmlN0WPP/6s0qdHjOjApwb9q1aoJTojld4SqdjBRr9tESJ"
                  + "RAmkuwAif7/poCBw1ydN57fbgPvU6oXOyGVvt55BUKRrk4ZLERIhBR3SVTS6YB2w353KWePt"
                  + "vxGBR78VSyhE5EWzE9ti13WzPAuXdDp7Ro8cgNdyXfEI1bFxiB4JXCYUTna3lCZC4WJfvZxA"
                  + "/duLhlMHhZJFIWttIMu4oRLVryQQCgIQpGog1PLvTQfFPat2eYFU1TnH1rUqoSCQSJDOdWtm"
                  + "Eh+nFuTL0TJl9DAJBbJhvqN50QgjhrwdBPO670of9JeM1DKRLTdbO+6/ze83IwGKTPZ77bVY"
                  + "azxC4ZpRI/UyoXBquqW0EAoOiIt+cZUVt82J3hGNUHnr3SpEX39o3n1WzFOMyt74YWYk2Jdj"
                  + "OrwVsSydIDHuNYkEBY0ODUM0Y4IcKUrgLte+gRn1kDKiaGEvBA1/QyQUTjDWkqVzFcqcqVD3"
                  + "FiGybLPaln2nUKtaJlmtb7fCbxScUD3FgfRq89gs4mpjEI9QrzxtFsIYNg2pkFtKC6HcduFU"
                  + "Qsi/UkHKYJdNK1UCGD1aRh0ehQ3cpSAoUvx72jnGGo/IgTvO2EESQY9X1e74Uc0oWFhpHu5U"
                  + "Uz+OltORHsJJEIURrSC4E1rfUHixC+5huDuioHIryar5ikEkOLZ77cD2wvmri0c8Qm3OUo0/"
                  + "YTCvDSmthELatmu7StlbYqOqey3/ndONSBAvukEPUqgmlYXIcKRqKHxAQDZU73ZsdM6H0juK"
                  + "IXB66+eZB4JGaR5RyC7oi8KHVf3DfWroa2YKiH64w0IHIpdFXkREe7HIfYjA3t82O22yz3mr"
                  + "PWMfEbXd2PuJQzxCWfP8DwAA//9/6nFUAAANkUlEQVTtWgm8TdUa/8507zUPiRTRoBIl8fQe"
                  + "noSUboZ4ZEgZX4pkKpRKGUKGl5eEQshQJEWpSCrXkJIMJZRknu7kDPvsYb3vv/bb99xz7znn"
                  + "Tmb7+/32WWfvvYa9vrW/4f9fm0QU6dsuIG5yesWpE0aUGufn8vSxQVH/Gp8Y3luJ+QBKwBBN"
                  + "qvjkHDCPFjV9Ysa4oNj7qx7Wbt9uXbwzISiqF/KK5JPmXJ/vGRC92wRkvfb1/bKPHT+Gt9M0"
                  + "QyyeqYoP3lHFmyODonZpb8ZYd5b0ioGPBMRHc1VxYJ/ZrkMDv/h8iSr7RP2Havvl/0P7ddH8"
                  + "Dp9YNF0VeOas8uogc569WgbEkYOhZ3iydUDMnBjMWv2yOfd5DaGqIX19vUKT+n/7tbOnk5ef"
                  + "UuQYu3eE1iGrwinrBev8XBvUyWOGwMudkxz8Uxc9Ev1i83dazKobv9ZE05t90lBgUJmPmsW9"
                  + "ok0dv+jxgF/0+VdArP5YFa1q+TL6fO/NoGhzl/nCY9HWfxV5rK8+0QRe7FqlwvvPPBb+z5oU"
                  + "FFgMGBJk1TJV3FXWK5YvUEXjG31i2TzT0LJOKC3FELfGeYXfZ4h/N/eLqaNCLwucA4z2cpVn"
                  + "uwQyHCB0AIcHXcOZnTweMrQzqZ+LxqDw4txdySeqxXulVy+oEg7/pYuOd/tFMGh6MRhf8xqh"
                  + "aAXFV/WYRlDvaq84nW6IwV0DYum75ou9Zb0m/lnRF/MxXuhleiv0NahzQEarrZs0cfyIIbb/"
                  + "oMvzkf0UMbR7QCiKISOQZQD7fzcX/4sPVeE9Hb74KacMAadhSZ0yXrFprSbmTA5K4zMMs37S"
                  + "ak20/ptp9Fbdy6nEunVuZK4x5o2M4cckTdxexCtefyl29pJfPV00BrVohipqlvCKTg3N9GpA"
                  + "p4D4+fvIUSGSMvCS/fGbLj19/44BcUcx01juuc4nHq7nF7cVNs8bXe8TY55RxE8bNIE26amG"
                  + "NAD0+dbooJj9HzMC4KVG1LEkPc0QSDUtWTpHld7Qet5oEcyqjxKLjagIwdjVEiKn0zCcT98P"
                  + "RSykelNGBKXXhcPZsMbUCyJ6w8qxjV4Odon+wGFC/1ll4jBF/L2cVzqxrPcKep4bg3JgEIog"
                  + "Tz+s0MrFOm04VohKXeGIUOPMXXquh0JLZunZOix7tYOq3emkylUcVKSYgxIKEaWlCko+zscJ"
                  + "olNc4jh+WJD3dKj5fW1c9I9GThr5tEqFixLd+5CL2nR1U616rlAl/nc6XVCzqgFq19NNn32g"
                  + "0eNDPNTyEbesU7e8j5IOF5b/k1bp1PU+hboNdFPKSUGfLtIp4Ce6+loHHT8iqPcwDz3xvCes"
                  + "76wn6fzcibcF6Jv9PAmW+6v6aebn8dyHU56nnBI0un+Qls3TacK8OHqwg/kcU0eptPYznRZ+"
                  + "l0D92itUktdi+JQ42abFHX56f30C6+Xsro8c7AL70XVBDp620xk+99RkQQ0r+Wk867BxC1OH"
                  + "Z+rRX+kbpPemaLRiWwLdeKu5btn6jma15wpDIS3r1z4gbnGHcAi8N1Kp3ByoC0/d7X6/jDKI"
                  + "VJYgAul6eEpl3UOJyGONgRRwzy+htlY0Qb0f1pmA16r72hBFkgrWeaxoinzeyukzRxSkK1Zq"
                  + "B5CL1O7FJxSJlaaNCUVDECQYB2not59ronuzkFce0s3Eb8BxFqGC573UBZjSImj279Xl+gA2"
                  + "fDjbjOyv9FXOStp3wUeo33cZNLCTQof+FLR8WyHav9eg7ZsNatLKRX4v0d5fDDr8l6CAj0hR"
                  + "BBUr4aArr+KjvIOKl3TQNZUdVKJUuIf6ZL5GDRNdsm427xHhQt92ihxn3LvxMhpaVeb+V6XO"
                  + "T5lRh19QalIlQHXudlLiw26q39RFnLXRnp2C0lIE1a4fHvmsPlDOnKDSohkala/okFENkQbS"
                  + "v6NCI6fHUZGiDurVUqE1y0MRukw5os59PJTM0XDeGxppGklvXKu+k+K5+cyVoT4O7hP061aD"
                  + "9UH0wuQ4atTCRR5PuE7kgJfAj88r6Pu1Bk0ernI24aYK1zlpcBeFbq/jJKfLQUtna9S4pUu+"
                  + "M41ZD8g4zqRcsBEKGGLuG0EJIOGZ1ywP4aWF01RJJkTysiAP4L23btQkTgLIR4TLLPDWkSh1"
                  + "MGxZ66LdsMeVDOyUuZ/M/xHlGlzrE8AteRU8oxXJMkeezIzmpBcUSZ2DqgdRYdVHCawARs+6"
                  + "Nn6oCbihwxpFs0dxsFz9OgTE+2+r0nOfOGpIzJbX5z5f9bFGIG2AFRFxgImQLT1Q3SdudmWf"
                  + "r6UX3AM+xvYFso1d20LZxpmaywUZofw+QX3aKJR6ihjXuKjVo24qVDjkUaeMUOnHJJ3eWBIf"
                  + "dh2eZvFMjZ7vGcxwOkWLE60/Woji4kLtf9tu0GNNArT+iIl/rMpjBgWpaWsX3VnXjCasZJo+"
                  + "VqPJL6n06Y4EqnRj5JwY9V7uo9K+3QbN/sKMDFafuSmBv/p3UGjoxDjGZy6ONKFnjdZ+3Zc6"
                  + "zeEIiUg8YFQclS7joKTVuozkie1dUi9KQDAOC9Ch/REhcFjXHnbU5a5xyKMU91WiNEkshugO"
                  + "fJxxXhrnxLjTQR6GaThc7PnzI0HOKPycWfhOM75NZ7yaJig1mSidIzqiehr/T+Xy5DE+jjIO"
                  + "ZiyK8uQx4q2c7CMWK0HUoJlLPveyuZrMWmr83Uk9n/HQxq91KlPOQT34f8Av6NghQdfeEHk9"
                  + "s/ec+yu5iVDnnJQIBgWdTiUqfWXkhcKL8kD1AF1R1kET58dRhcohxWxaq9NLTwbphqpOepBf"
                  + "LJ1TocT22YFn92YBQgqHPixZsVCjcYNVeuxpN5VjsmP+Wxpt/tagLv3cNHSCCfKtulZ55IBB"
                  + "I/qq9P03On24OSHsWaw6sUoQJkjnqtd20oucjsWSrRt1WsFkxzNjPTmmbLu2malyoSIOGjja"
                  + "Q+vYaFNOIk10044fDNrDqfIfuwQfBh3kdFoJxBo59j3Yv5sNMu7/BmYamkNewz2sgc7ZKo4g"
                  + "rx3IGowXyShij8Tj8FLCiYDsqXCdQ67zTdWdVKU6p/eVHDGdEYwUhERFTgPPlpwRg1rwbbzE"
                  + "K1kfsvJNrFR36IXNej8/5yfYQxUpRrRzi0Gd71EkXhg0xkMdn3CHKVNVRcyX7q3RKrVmVq8s"
                  + "L44lYIW6NlXYmxnWJRmxJi2IyzYPLAywy4xxqqz75tJ4qtskhJMO7TfY81JUpgdOY80nOg3v"
                  + "HSSVA+pSNsaK10de6J826DRpmErbGDvCsNt2z+4grAdGtHz3dY3GD1UJ0Xnemhhsk9WIy1Mn"
                  + "BB09IOgIH4gEYElTeI6pzCwiUwADiSjiZdZTlhxVgFthJPkRF6sK6whmFmXR4g7GtETFOSKW"
                  + "RBQswxGSSzCWiJhlGBdjrXA9NxE80jONHhCkOZM16veKh3o9VzDsdIxZ4zTWT1aZyu/V8gXs"
                  + "+ArC8lk5atby6KG844louSz2hbDfgn0jC6fgsxprTHy1gL0qsHa5kUhYCe3QHnk5NlyBVTJ/"
                  + "uoL7v23XBTZssTmIsRNv84Uxf79s1cVTbc19Lmy2ZpaeD/pFs2o+iYWwX4T2+Jzpm5Xh9TK3"
                  + "wRzxxQT2mVKTY8/tyAFdPNrYLz+nwuc1YLUgmAOw5dkQ9G3t1f31hy73+sCEAp/s3KKLX3/W"
                  + "5adcwDzYTMfagYE7HwL9Q+fvjA8xpPl9DuBq692LVMb69Chqyoe9jz8ZN0ST9r3cYdglWr1Y"
                  + "15FXD+4SJKRyj/Z18+EJY+1mTVJp/BBVslzoBwwXWJxWncG0OfOd31vPBM+8YY1OSasMPnTG"
                  + "SaZXwv4XWKR2PXiO8Q58nkWj+qv0wdsatenmph6D3Bn7R1Zfmiboy6U6rf5Ypy1JBlW8wUHP"
                  + "jo2jW2tGjkxoN+1VlTr1dksPbvUTrQQDmpZCVL2W2R90hyj63lSNpiyJY6YrFEGj9XEpXwfr"
                  + "CqYUDGx8Qigzyc+c13+l027G4tGkBe9VIsJGkqgGFanymb42gCnzKrxB1pmNqSinB5EEZMBr"
                  + "jH1WLQvPP0AtN23t5lTKBNsA3VdV4NSBjSEzSYH0EPgCeCaZUx8cu3cYtO5Lg37eZDD9bY6K"
                  + "TeN697roXqbsAfxhSJZg0/enDQb1fNYjwa91/XyVzITShOdUuqWGk/q86KG7Gl7exmStA9Z6"
                  + "BadkSWwQ42bHW5fPaXleDQq4JrcsEoA49phWLGS2Kwdmq/SVZv6OrxrSmQCJJmC07mnu4h11"
                  + "l/RsF8MXBysXazSfo1K/EZ4MxjLa/C6X69ifWjhNo1mTNDL4nZr6Ufx5i9jn1aDys+BIv3Zu"
                  + "ERxddNrOjBbA/J4dIiaABoAH6EX0wlGeWaQG97moZt2Cp435mUNB2iDVA8i3xdQAKHNsS4AA"
                  + "6c6fhj30mLvAKV9BdHvRGVSkySJ/RiQCMwOcAQoVey8wHtCwmfe5IrW3r12cGkCK1zNRobaM"
                  + "a+9v68p1tnM2Z3tJGNTZVJDdt62BvGjANqi8aMuua2sgBw3YBpWDguzbtgbyogHboPKiLbuu"
                  + "rYEcNGAbVA4Ksm/bGsiLBmyDyou27Lq2BnLQgG1QOSjIvm1rIC8asA0qL9qy69oayEEDtkHl"
                  + "oCD7tq2BvGjANqi8aMuua2sgBw3YBpWDguzbtgbyogHboPKiLbuurYEcNPA/hiPAN+sqVBQA"
                  + "AAAASUVORK5CYII=");
            return new ImageView(new Image(new ByteArrayInputStream(b)));
        case BROKEN_NAV:
            return 
                "To reproduce:\n" +
                "place the cursor on a line following \"Ỏ̷͖͈̞̩͎̻̫̫̜͉̠̫͕̭̭̫̫̹̗̹͈̼̠̖͍͚̥͈̮̼͕̠̤̯̻̥̬̗̼̳̤̳̬̪̹͚̞̼̠͕̼̠̦͚̫͔̯̹͉͉̘͎͕̼̣̝͙̱̟̹̩̟̳̦̭͉̮̖̭̣̣̞̙̗̜̺̭̻̥͚͙̝̦̲̱͉͖͉̰̦͎̫̣̼͎͍̠̮͓̹̹͉̤̰̗̙͕͇͔̱͕̭͈̳̗̭͔̘̖̺̮̜̠͖̘͓̳͕̟̠̱̫̤͓͔̘̰̲͙͍͇̙͎̣̼̗̖͙̯͉̠̟͈͍͕̪͓̝̩̦̖̹̼̠̘̮͚̟͉̺̜͍͓̯̳̱̻͕̣̳͉̻̭̭̱͍̪̩̭̺͕̺̼̥̪͖̦̟͎̻̰_Ỏ̷͖͈̞̩͎̻̫̫̜͉̠̫͕̭̭̫̫̹̗̹͈̼̠̖͍͚̥͈̮̼͕̠̤̯̻̥̬̗̼̳̤̳̬̪̹͚̞̼̠͕̼̠̦͚̫͔̯̹͉͉̘͎͕̼̣̝͙̱̟̹̩̟̳̦̭͉̮̖̭̣̣̞̙̗̜̺̭̻̥͚͙̝̦̲̱͉͖͉̰̦͎̫̣̼͎͍̠̮͓̹̹͉̤̰̗̙͕͇͔̱͕̭͈̳̗̭͔̘̖̺̮̜̠͖̘͓̳͕̟̠̱̫̤͓͔̘̰̲͙͍͇̙͎̣̼̗̖͙̯͉̠̟͈͍͕̪͓̝̩̦̖̹̼̠̘̮͚̟͉̺̜͍͓̯̳̱̻͕̣̳͉̻̭̭̱͍̪̩̭̺͕̺̼̥̪͖̦̟͎̻̰\"\n" +
                "and try going back with arrow-left key.";
        default:
            return "?" + d;
        }
    }
}
