package Other.interview;

import java.math.BigInteger;

public class DH {
    //公开的A和B都知道的素数和素数的原根
    public static final BigInteger ROOT = BigInteger.valueOf(5);
    public static final BigInteger PRIME = BigInteger.valueOf(97);

    public static void main(String[] args) {
        ShyMan a = new ShyMan(888);
        int mixtureA = a.getMixture();
        ShyMan b = new ShyMan(222);
        int mixtureB = b.getMixture();

        //mixtureA and mixtureB will be transmitted in network, but it is meaningless for anyone
        System.out.println(mixtureA);
        System.out.println(mixtureB);

        //Alice and Bob will get the same key number from each other
        System.out.println(a.getCommonKey(mixtureB));
        System.out.println(b.getCommonKey(mixtureA));
    }
}

class ShyMan {
    //自己的私有密值,不会告诉任何人
    private int private_key_number;

    public ShyMan(int private_key_number) {
        this.private_key_number = private_key_number;
    }

    public int getMixture() {
        return DH.ROOT.pow(private_key_number).mod(DH.PRIME).intValue();
    }

    public int getCommonKey(int mixture) {
        return BigInteger.valueOf(mixture).pow(private_key_number).mod(DH.PRIME).intValue();
    }
}