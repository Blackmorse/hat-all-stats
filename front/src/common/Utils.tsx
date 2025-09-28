export function toArabian(division: string): number {
    switch(division) {
        case "I":
            return 1;
        case "II":
            return 2;
        case "III":
            return 3;
        case "IV":
            return 4;
        case "V":
            return 5;
        case "VI":
            return 6;
        case "VII":
            return 7;
        case "VIII":
            return 8;
        case "IX":
            return 9;
        case "X":
            return 10;
    }
    return -1
}

export function toRoman(division: number): string {
    switch(division) {
        case 1:
            return "I";
        case 2:
            return "II";
        case 3:
            return "III";
        case 4:
            return "IV";
        case 5:
            return "V";
        case 6:
            return "VI";
        case 7:
            return "VII";
        case 8:
            return "VIII";
        case 9:
            return "IX";
        case 10:
            return "X";
    }
    return ""
}

function splitLeagueUnit(leagueUnit: string): [string, number?] {
    const split = leagueUnit.split('.')
    if (split.length !== 2) {
        return [leagueUnit]
    }
    if (isNaN(Number(split[1]))) {
        return [leagueUnit]
    }
    return [split[0], Number(split[1])]
}

const numberLeagueUnitsMap: Map<string, number> = new Map<string, number>([
    ['II', 4],
    ['III', 16],
    ['IV', 64],
    ['V', 256],
    ['VI', 1024],
    ['VII', 1024],
    ['VIII', 2048],
    ['IX', 2048]
])

export function nextLeagueUnit(leagueUnit: string): string | undefined {
    const [division, leagueUnitNumber] = splitLeagueUnit(leagueUnit)
    if (leagueUnitNumber === undefined) {
        return undefined
    }
    if (leagueUnitNumber === numberLeagueUnitsMap.get(division)) {
        return undefined
    }
    return division + '.' + (leagueUnitNumber + 1)
}

export function previousLeagueUnit(leagueUnit: string): string | undefined {
    const [division, leagueUnitNumber] = splitLeagueUnit(leagueUnit)
    if (leagueUnitNumber === undefined) {
        return undefined
    }
    if (leagueUnitNumber === 1) {
        return undefined
    }
    return division + '.' + (leagueUnitNumber -1 )
}
