$(function () {
    // 点击出现下拉菜单
    $('.lv1-li span').each(function () {
        $(this).click(function () {
            if ($(this).children('i').attr('class') == 'i_active') {
                $(this).children('i').removeClass('i_active');
                $(this).next('.lv2-ul').slideUp();
            } else {
                $(this).children('i').addClass('i_active');
                $(this).next('.lv2-ul').slideDown();
                $(this).parent('.lv1-li').siblings().children('span').children('i').removeClass('i_active');
                $(this).parent('.lv1-li').siblings().children('.lv2-ul').slideUp();
            }
        });
    });

    $('.lv2-li').each(function () {
        $(this).click(function () {
            $(".lv2-li_a_active").removeClass("lv2-li_a_active");
            $(this).find('a').addClass('lv2-li_a_active');
        });
    });
});