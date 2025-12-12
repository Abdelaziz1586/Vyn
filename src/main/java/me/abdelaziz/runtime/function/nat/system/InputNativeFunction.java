package me.abdelaziz.runtime.function.nat.system;

import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;

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
