# rocket

A Quil sketch designed to ... well, that part is up to you.

## Setting Up Dependencies

- Create a 'checkouts' folder in the directory where this readme is located
- Inside checkouts, create a symlink to '../../basic-particles'. Call it whatever you want
- Go to the basic-particles project (a sibling to this project) and run 'lein install'. This will create a .jar file and store it in your local repository (mine is located at ~/.m2)
- Now you can go to the root of this project and start 'lein run'

## Additional Info

- Creating a symlink: http://apple.stackexchange.com/questions/115646/how-can-i-create-a-symbolic-link-in-terminal
- Requiring files from other projects: http://stackoverflow.com/a/20281067/138392

## Usage

LightTable - open `core.clj` and press `Ctrl+Shift+Enter` to evaluate the file.

Emacs - run cider, open `core.clj` and press `C-c C-k` to evaluate the file.

REPL - run `(require 'rocket.core)`.

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
