import LawModel from './model';

export default filterLaws = (res, today, {affects, start}) => {
    LawModel.find({
        $or: [
            {affects: {$in: affects}},
            {affects: {$size: 0}}
        ]
    }, (err, laws) => {
        const todayDate = new Date(today);
        laws.filter(({timeWindow, affectsIn}) => {
            const condTW = timeWindow === null || (todayDate >= timeWindow.start && todayDate <= timeWindow.end);
            const condAI = affectsIn === 0 || todayDate - start;
            return condTW && condAI;
        });
    });
};