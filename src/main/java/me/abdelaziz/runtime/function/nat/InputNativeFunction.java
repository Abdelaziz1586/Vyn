package me.abdelaziz.runtime.function.nat;

import me.abdelaziz.runtime.Value;

import java.util.Scanner;

public final class InputNativeFunction extends NativeFunction {

    public InputNativeFunction() {
        super((env, args) -> {
            if (!args.isEmpty())
                System.out.print(args.get(0));

            return new Value(new Scanner(System.in).nextLine());
        });
    }

}
