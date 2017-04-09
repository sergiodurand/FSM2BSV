import os, argparse

parser = argparse.ArgumentParser()
parser.add_argument("converter", choices=["dec2hex", "hex2dec"], help="Choose a conversion type")
parser.add_argument("input", help="Input file")
parser.add_argument("output", help="Output file")
parser.add_argument('-v', '--version', action='version', version='%(prog)s 1.0.1')
args = parser.parse_args()

if args.converter == 'dec2hex':
    print "Decimal to Hex selected"
    lines = [l.strip() for l in open(args.input, 'r').readlines() if len(l.strip()) > 0];
    elements = [el.split(' ') for el in lines];
    numbers = [int(float(num)) for n in elements for num in n if (len(num.strip()) > 0)]
    hexs = [hex(n)[2:].upper() for n in numbers]
    open(args.output,'w').write(os.linesep.join(hexs));
    print "Conversion done."

""" TODO: make a better output (matrix M x N) to be able to load into octave """
if args.converter == 'hex2dec':
    print "Hex to Decimal selected"
    print "Not implemented yet."
