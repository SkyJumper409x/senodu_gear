#!/bin/bash

yell() { echo "$0: $*"; echo "$0: $*" >&2; };
die() { yell "$*"; exit 111; };

if [ $# -eq 0 ]; then
    die "not enough arguments"
fi
if [ "$1" = "--help" ] || [ "$1" = "-help" ] || [ "$1" = "-h" ]; then
    echo -e 'Usage:
sendou_res_convert.sh {--help,-help,-h}\tprints this message
sendou_res_convert.sh <sendou_dir>\tactually does the setup stuffs

sendou_dir should be the directory into which https://github.com/sendou-ink/sendou.ink was cloned
From the sendou.ink stuff, the following directories must be present:
sendou.ink/locales/
sendou.ink/public/static-assets/img/abilities/
'
fi

sendou_dir="$1"
input_dir="$sendou_dir/locales"
ability_images_dir="$sendou_dir/public/static-assets/img/abilities"
if [ ! -d "$input_dir" ]; then
    die "input_dir is not a directory"
fi
if [ ! -d "$ability_images_dir" ]; then
    die "ability_images_dir is not a directory"
fi

res_dir="sendougear/src/main/resources"
if [ $# -eq 2 ]; then
    res_dir="$2"
fi
if [ ! -d "$res_dir" ]; then
    die "res_dir is not a directory"
fi

abilities_jsonfile="$res_dir/abilities.json"
if [ ! -f "$abilities_jsonfile" ]; then
    die "abilities_jsonfile is not a file"
fi

output_dir="$res_dir/lang"
if [ ! -d "$output_dir" ]; then
    mkdir "$output_dir"
fi
imgs_output_dir="$res_dir/img/ability"
if [ ! -d "$imgs_output_dir" ]; then
    mkdir -p "$imgs_output_dir"
fi

internal_names='"ABILITY_('"$(cat "$abilities_jsonfile" | tail -c+2 | tr '\n' @ | tr } '\n' | sed -E 's/(,?@  )?\{@ +"short".+?"(.+)",@ +.+: "(.+)"@  /\2/g' | tr '\n' \| | head -c-4)"')'
# echo "$internal_names"

conv_file() {
    in_f="$input_dir/$1/game-misc.json"
    out_fn="$1"
    if [ $# -eq 2 ]; then
        out_fn="$2"
    fi
    out_f="$output_dir/$(echo $out_fn | tr '-' '_').json"
    echo $'{\n'"$(cat "$in_f" | grep -iE "$internal_names" | head -c-2)"$'\n}' > "$out_f"
}
langs=($(ls -1 "$input_dir"))
for lang in "${langs[@]}"; do
    conv_file "$lang"
done

cp -t "$imgs_output_dir" "$ability_images_dir"/*.png
