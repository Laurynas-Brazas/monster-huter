package com.mygdx.game.world;

import java.util.HashMap;

public enum TileType {
    NOTHING1(1, true, "Nothing"),
    NOTHING2(2, true, "Nothing"),
    NOTHING3(3, true, "Nothing"),
    NOTHING4(4, true, "Nothing"),
    NOTHING5(5, true, "Nothing"),
    NOTHING6(6, true, "Nothing"),
    NOTHING7(7, true, "Nothing"),
    NOTHING8(8, false, "Nothing"),
    NOTHING9(9, false, "Nothing"),
    NOTHING10(10, false, "Nothing"),
    NOTHING11(11, false, "Nothing"),
    NOTHING12(12, false, "Nothing"),
    NOTHING13(13, false, "Nothing"),
    NOTHING27(27, true, "Nothing"),
    NOTHING28(28, true, "Nothing"),
    NOTHING29(29, true, "Nothing"),
    NOTHING30(30, true, "Nothing"),
    NOTHING31(31, true, "Nothing"),
    NOTHING32(32, true, "Nothing"),
    NOTHING33(33, true, "Nothing"),
    NOTHING34(34, false, "Nothing"),
    NOTHING35(35, false, "Nothing"),
    NOTHING36(36, false, "Nothing"),
    NOTHING37(37, false, "Nothing"),
    NOTHING38(38, false, "Nothing"),
    NOTHING39(39, false, "Nothing"),
    NOTHING53(53, true, "Nothing"),
    NOTHING54(54, true, "Nothing"),
    NOTHING55(55, true, "Nothing"),
    NOTHING56(56, true, "Nothing"),
    NOTHING57(57, true, "Nothing"),
    NOTHING58(58, true, "Nothing"),
    NOTHING59(59, true, "Nothing"),
    NOTHING60(60, true, "Nothing"),
    NOTHING61(61, true, "Nothing"),
    NOTHING62(62, true, "Nothing"),
    NOTHING63(63, true, "Nothing"),
    NOTHING64(64, true, "Nothing"),
    NOTHING79(79, true, "Nothing"),
    NOTHING80(80, true, "Nothing"),
    NOTHING81(81, true, "Nothing"),
    NOTHING82(82, true, "Nothing"),
    NOTHING83(83, true, "Nothing"),
    NOTHING84(84, true, "Nothing"),
    NOTHING85(85, true, "Nothing"),
    NOTHING86(86, true, "Nothing"),
    NOTHING87(87, true, "Nothing"),
    NOTHING88(88, true, "Nothing"),
    NOTHING89(89, true, "Nothing"),
    NOTHING90(90, true, "Nothing"),
    NOTHING91(91, true, "Nothing"),
    NOTHING105(105, true, "Nothing"),
    NOTHING106(106, true, "Nothing"),
    NOTHING107(107, true, "Nothing"),
    NOTHING108(108, true, "Nothing"),
    NOTHING109(109, true, "Nothing"),
    NOTHING110(110, true, "Nothing"),
    NOTHING111(111, true, "Nothing"),
    NOTHING112(112, true, "Nothing"),
    NOTHING113(113, true, "Nothing"),
    NOTHING114(114, true, "Nothing"),
    NOTHING115(115, true, "Nothing"),
    NOTHING116(116, true, "Nothing"),
    NOTHING117(117, true, "Nothing"),
    NOTHING131(131, true, "Nothing"),
    NOTHING132(132, true, "Nothing"),
    NOTHING133(133, true, "Nothing"),
    NOTHING134(134, true, "Nothing"),
    NOTHING135(135, true, "Nothing"),
    NOTHING136(136, true, "Nothing"),
    NOTHING137(137, true, "Nothing"),
    NOTHING138(138, true, "Nothing"),
    NOTHING139(139, true, "Nothing"),
    NOTHING140(140, true, "Nothing"),
    NOTHING141(141, true, "Nothing"),
    NOTHING142(142, true, "Nothing"),
    NOTHING143(143, true, "Nothing"),
    NOTHING157(157, true, "Nothing"),
    NOTHING158(158, true, "Nothing"),
    NOTHING159(159, true, "Nothing"),
    NOTHING160(160, true, "Nothing"),
    NOTHING161(161, true, "Nothing"),
    NOTHING162(162, true, "Nothing"),
    NOTHING163(163, true, "Nothing"),
    NOTHING164(164, true, "Nothing"),
    NOTHING165(165, true, "Nothing"),
    NOTHING166(166, true, "Nothing"),
    NOTHING167(167, true, "Nothing"),
    NOTHING168(168, true, "Nothing"),
    NOTHING169(169, true, "Nothing"),
    NOTHING183(183, true, "Nothing"),
    NOTHING184(184, true, "Nothing"),
    NOTHING185(185, true, "Nothing"),
    NOTHING186(186, true, "Nothing"),
    NOTHING187(187, true, "Nothing"),
    NOTHING188(188, true, "Nothing"),
    NOTHING189(189, true, "Nothing"),
    NOTHING190(190, true, "Nothing"),
    NOTHING191(191, true, "Nothing"),
    NOTHING192(192, true, "Nothing"),
    NOTHING193(193, true, "Nothing"),
    NOTHING194(194, true, "Nothing"),
    NOTHING195(195, true, "Nothing"),
    NOTHING209(209, true, "Nothing"),
    NOTHING210(210, true, "Nothing"),
    NOTHING211(211, true, "Nothing"),
    NOTHING212(212, false, "Nothing"),
    NOTHING213(213, false, "Nothing"),
    NOTHING214(214, false, "Nothing"),
    NOTHING215(215, false, "Nothing"),
    NOTHING216(216, false, "Nothing"),
    NOTHING217(217, false, "Nothing"),
    NOTHING218(218, false, "Nothing"),
    NOTHING219(219, false, "Nothing"),
    NOTHING220(220, true, "Nothing"),
    NOTHING221(221, true, "Nothing"),
    NOTHING235(235, true, "Nothing"),
    NOTHING236(236, true, "Nothing"),
    NOTHING237(237, true, "Nothing"),
    NOTHING238(238, false, "Nothing"),
    NOTHING239(239, false, "Nothing"),
    NOTHING240(240, false, "Nothing"),
    NOTHING241(241, false, "Nothing"),
    NOTHING242(242, false, "Nothing"),
    NOTHING243(243, false, "Nothing"),
    NOTHING244(244, false, "Nothing"),
    NOTHING245(245, false, "Nothing"),
    NOTHING246(246, true, "Nothing"),
    NOTHING247(247, true, "Nothing"),
    NOTHING261(261, true, "Nothing"),
    NOTHING262(262, true, "Nothing"),
    NOTHING263(263, true, "Nothing"),
    NOTHING264(264, false, "Nothing"),
    NOTHING265(265, false, "Nothing"),
    NOTHING266(266, false, "Nothing"),
    NOTHING267(267, false, "Nothing"),
    NOTHING268(268, false, "Nothing"),
    NOTHING269(269, false, "Nothing"),
    NOTHING270(270, false, "Nothing"),
    NOTHING271(271, false, "Nothing"),
    NOTHING272(272, true, "Nothing"),
    NOTHING273(273, true, "Nothing"),
    NOTHING287(287, true, "Nothing"),
    NOTHING288(288, true, "Nothing"),
    NOTHING289(289, true, "Nothing"),
    NOTHING290(290, false, "Nothing"),
    NOTHING291(291, false, "Nothing"),
    NOTHING292(292, false, "Nothing"),
    NOTHING293(293, false, "Nothing"),
    NOTHING294(294, false, "Nothing"),
    NOTHING295(295, false, "Nothing"),
    NOTHING296(296, false, "Nothing"),
    NOTHING297(297, false, "Nothing"),
    NOTHING298(298, true, "Nothing"),
    NOTHING299(299, true, "Nothing"),
    NOTHING313(313, true, "Nothing"),
    NOTHING314(314, true, "Nothing"),
    NOTHING315(315, true, "Nothing"),
    NOTHING316(316, false, "Nothing"),
    NOTHING317(317, false, "Nothing"),
    NOTHING318(318, false, "Nothing"),
    NOTHING319(319, false, "Nothing"),
    NOTHING320(320, false, "Nothing"),
    NOTHING321(321, false, "Nothing"),
    NOTHING322(322, false, "Nothing"),
    NOTHING323(323, false, "Nothing"),
    NOTHING324(324, false, "Nothing"),
    NOTHING325(325, false, "Nothing");

    public static final int TILE_SIZE = 64;

    private int id;
    private boolean collidable;
    private String name;
    private float damage;
    private TileType(int id, boolean collidable, String name) {
        this(id, collidable, name, 0);
    }

    private TileType (int id, boolean collidable, String name, float damage) {
        this.id = id;
        this.collidable = collidable;
        this.name = name;
        this.damage = damage;
    }

    public int getId() {
        return id;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public String getName() {
        return name;
    }

    public float getDamage() {
        return damage;
    }

    private static HashMap<Integer, TileType> tileMap;

    static {
        tileMap = new HashMap<Integer, TileType>();
        for(TileType tileType : TileType.values())
           tileMap.put(tileType.getId(), tileType);
    }

    public static TileType getTileTypeById(int id) {
        return tileMap.get(id);
    }
}
