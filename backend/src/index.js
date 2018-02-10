import { mergeSchemas } from 'graphql-tools';

import laws from './laws/schema';
import zones from './zones/schema';

const schema = mergeSchemas({
    schemas: [laws, zones],
});

export default schema;