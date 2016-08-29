# boxy-press

A Quil sketch designed to ... well, that part is up to you.

## Setting Up Dependencies

- Go to the basic-ces project (root of this repo) and run 'lein install'. This will create a .jar file and store it in your local repository (mine is located at ~/.m2)
- Now you can go to the root of this project and start 'lein run'

### Additional Info

- Requiring files from other projects: http://stackoverflow.com/a/20281067/138392
- Creating symlinks: http://stackoverflow.com/a/16321120/138392

## Usage

LightTable - open `core.clj` and press `Ctrl+Shift+Enter` to evaluate the file.

Emacs - run cider, open `core.clj` and press `C-c C-k` to evaluate the file.

REPL - run `(require 'boxy-press.core)`.

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
