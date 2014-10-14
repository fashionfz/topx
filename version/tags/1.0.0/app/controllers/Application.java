package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("码农们正在主线上疯狂的开发!"));
        //return ok(index.render("到了5月1日，测试已经在主线上测试完毕,我们要进行v.1.0.0.RELEASE"));
    }

}
